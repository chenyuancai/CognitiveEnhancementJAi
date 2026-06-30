#!/usr/bin/env python3
"""修正 JavaDoc 位置：注释必须在注解之前；合并重复类注释；改进异常处理方法描述。"""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

ANN = re.compile(r"^\s*@\w+")
TYPE = re.compile(
    r"^\s*(?:(?:public|protected|private)\s+)?"
    r"(?:(?:abstract|static|final|sealed|non-sealed)\s+)*"
    r"(?:(?:class|interface|enum|record)\s+\w+|@interface\s+\w+)"
)
METHOD = re.compile(
    r"^\s*(?:(?:public|protected|private)\s+)"
    r"(?:(?:static|final|synchronized)\s+)*"
    r"(?:<[^>]+>\s+)?[\w.<>,\[\]?]+\s+\w+\s*\("
)
FIELD = re.compile(
    r"^\s*(?:private|protected|public)\s+"
    r"(?:(?:static)\s+)?(?:(?:final)\s+)?"
    r"[\w.<>,\[\]?]+\s+\w+\s*(?:=\s*[^;]+)?;\s*$"
)

CTOR = re.compile(r"^\s*(?:(?:public|protected|private)\s+)(?P<name>\w+)\s*\(")

HANDLE_METHOD_DESC = {
    "handleServiceException": ("处理业务异常", "业务异常", "统一错误响应"),
    "handleValidation": ("处理参数校验异常", "校验异常", "统一错误响应"),
    "handleUnreadable": ("处理请求体不可读异常", "解析异常", "统一错误响应"),
    "handleUnknown": ("处理未捕获异常", "异常", "统一错误响应"),
    "handleNotFound": ("处理资源不存在异常", "异常", "统一错误响应"),
    "handleForbidden": ("处理权限不足异常", "异常", "统一错误响应"),
    "handleBadRequest": ("处理非法请求异常", "异常", "统一错误响应"),
}


def is_blank(line: str) -> bool:
    return not line.strip()


def is_annotation_line(line: str) -> bool:
    return bool(ANN.match(line))


def read_javadoc(lines: list[str], start: int) -> tuple[int, list[str]]:
    if start >= len(lines) or not lines[start].strip().startswith("/**"):
        return start, []
    end = start
    while end < len(lines):
        if lines[end].strip().endswith("*/"):
            return end, lines[start : end + 1]
        end += 1
    return start, lines[start:]


def read_annotations(lines: list[str], start: int) -> tuple[int, list[str]]:
    if start >= len(lines) or not is_annotation_line(lines[start]):
        return start, []
    i = start
    block = []
    while i < len(lines):
        if not is_annotation_line(lines[i]) and not block:
            break
        if is_annotation_line(lines[i]):
            block.append(lines[i])
            i += 1
            # 多行注解参数
            while block and "(" in block[-1] and ")" not in block[-1] and i < len(lines):
                block.append(lines[i])
                i += 1
            continue
        if block and "(" in "".join(block) and ")" not in "".join(block):
            block.append(lines[i])
            i += 1
            continue
        break
    return i, block


def skip_blanks(lines: list[str], idx: int) -> int:
    while idx < len(lines) and is_blank(lines[idx]):
        idx += 1
    return idx


def parse_javadoc_meta(block: list[str]) -> dict:
    desc: list[str] = []
    author = date = None
    for line in block[1:-1]:
        s = line.strip()
        if not s or s == "*":
            continue
        if s.startswith("* @author"):
            author = s.split("@author", 1)[1].strip()
        elif s.startswith("* @date"):
            date = s.split("@date", 1)[1].strip()
        elif s.startswith("* @"):
            continue
        else:
            desc.append(s.lstrip("* ").strip())
    return {"desc": desc, "author": author, "date": date}


def merge_javadoc(primary: list[str], secondary: list[str]) -> list[str]:
    p, s = parse_javadoc_meta(primary), parse_javadoc_meta(secondary)
    desc = p["desc"] or s["desc"] or ["类说明"]
    author = p["author"] or s["author"] or "cyc"
    date = p["date"] or s["date"] or "2026/6/15 14:18"
    indent = re.match(r"^(\s*)", primary[0]).group(1)  # type: ignore
    out = [indent + "/**", indent + f" * {desc[0]}"]
    for d in desc[1:]:
        out.append(indent + f" * {d}")
    out += [indent + " *", indent + f" * @author {author}", indent + f" * @date {date}", indent + " */"]
    return out


def find_prev_javadoc(result: list[str]) -> tuple[int, int] | None:
    j = len(result) - 1
    while j >= 0 and is_blank(result[j]):
        j -= 1
    if j < 0 or not result[j].strip().endswith("*/"):
        return None
    end = j
    while j >= 0 and "/**" not in result[j]:
        j -= 1
    return (j, end) if j >= 0 else None


def improve_method_javadoc(block: list[str], method_line: str) -> list[str]:
    m = re.search(r"\s+(\w+)\s*\(", method_line)
    if not m:
        return block
    name = m.group(1)
    indent = re.match(r"^(\s*)", block[0]).group(1)  # type: ignore
    if name in HANDLE_METHOD_DESC:
        desc, param_desc, ret_desc = HANDLE_METHOD_DESC[name]
        return [
            indent + "/**",
            indent + f" * {desc}。",
            indent + " *",
            indent + f" * @param exception {param_desc}",
            indent + f" * @return {ret_desc}",
            indent + " */",
        ]
    new_block = []
    for line in block:
        if "@param exception exception" in line:
            new_block.append(line.replace("@param exception exception", "@param exception 异常对象"))
        elif "@return 执行结果" in line and "ResponseEntity" in method_line:
            new_block.append(line.replace("@return 执行结果", "@return 统一错误响应"))
        elif line.strip() == " * 处理请求。":
            # 根据方法名生成
            if name.startswith("handle"):
                action = name[6:]
                new_block.append(indent + f" * 处理{action}相关异常。")
            else:
                new_block.append(line)
        else:
            new_block.append(line)
    return new_block


def is_signature(line: str) -> str | None:
    if TYPE.match(line):
        return "type"
    if METHOD.match(line):
        return "method"
    if CTOR.match(line):
        return "ctor"
    if FIELD.match(line):
        return "field"
    return None


def polish_method_comments(content: str) -> str:
    """改进已正确放置但内容粗糙的方法注释。"""
    lines = [l.rstrip("\n") for l in content.splitlines()]
    out: list[str] = []
    i = 0
    while i < len(lines):
        if lines[i].strip().startswith("/**"):
            jd_end, jd = read_javadoc(lines, i)
            k = skip_blanks(lines, jd_end + 1)
            if k < len(lines) and is_annotation_line(lines[k]):
                ann_end, ann = read_annotations(lines, k)
                m = skip_blanks(lines, ann_end)
                if m < len(lines) and METHOD.match(lines[m]) and any(
                    "@ExceptionHandler" in a for a in ann
                ):
                    improved = improve_method_javadoc(jd, lines[m])
                    if improved != jd:
                        out.extend(improved)
                        i = jd_end + 1
                        continue
            out.extend(jd)
            i = jd_end + 1
            continue
        out.append(lines[i])
        i += 1
    text = "\n".join(out)
    return text + ("\n" if content.endswith("\n") else "")


def rebuild(content: str) -> str:
    lines = [l.rstrip("\n") for l in content.splitlines()]
    out: list[str] = []
    i = 0

    while i < len(lines):
        # 注解块后紧跟 JavaDoc（错位）
        if is_annotation_line(lines[i]):
            ann_end, ann = read_annotations(lines, i)
            j = skip_blanks(lines, ann_end)
            if j < len(lines) and lines[j].strip().startswith("/**"):
                jd_end, jd = read_javadoc(lines, j)
                k = skip_blanks(lines, jd_end + 1)
                if k < len(lines) and (sig := is_signature(lines[k])):
                    if sig in ("method", "ctor"):
                        jd = improve_method_javadoc(jd, lines[k])
                    if sig == "type":
                        prev = find_prev_javadoc(out)
                        if prev:
                            ps, pe = prev
                            out = out[:ps] + merge_javadoc(out[ps : pe + 1], jd)
                        else:
                            out.extend(jd)
                        out.extend(ann)
                        i = jd_end + 1
                        continue
                    # field / method / ctor
                    out.extend(jd)
                    out.extend(ann)
                    i = jd_end + 1
                    continue

            out.extend(ann)
            i = ann_end
            continue

        # 字段：注解行后已有正确 JavaDoc 在更前 — 检测 @X \n /** \n private 不会出现
        # 正常 JavaDoc
        if lines[i].strip().startswith("/**"):
            jd_end, jd = read_javadoc(lines, i)
            out.extend(jd)
            i = jd_end + 1
            continue

        out.append(lines[i])
        i += 1

    return polish_method_comments("\n".join(out) + ("\n" if content.endswith("\n") else ""))


def main() -> int:
    files = sorted(ROOT.glob("**/src/main/java/**/*.java"))
    modified = 0
    for path in files:
        original = path.read_text(encoding="utf-8")
        fixed = rebuild(original)
        if fixed != original:
            path.write_text(fixed, encoding="utf-8")
            modified += 1
    print(f"Fixed placement in {modified}/{len(files)} files.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
