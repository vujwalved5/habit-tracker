$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open("C:\Users\aruko\OneDrive\Desktop\Documents\widget\Cloudflare_Worker_Setup_Guide.pdf")
$text = $doc.Content.Text
$text | Out-File -FilePath "C:\Users\aruko\OneDrive\Desktop\Documents\widget\habit-sync-worker\guide_text.txt" -Encoding utf8
$doc.Close()
$word.Quit()
