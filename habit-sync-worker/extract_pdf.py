from PyPDF2 import PdfReader
import sys, io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')

reader = PdfReader(r'C:\Users\aruko\OneDrive\Desktop\Documents\widget\Cloudflare_Worker_Setup_Guide.pdf')
for i, page in enumerate(reader.pages):
    text = page.extract_text()
    if text:
        print(f"=== PAGE {i+1} ===")
        print(text)
        print()
