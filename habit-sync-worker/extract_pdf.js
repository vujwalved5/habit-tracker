const fs = require('fs');
const zlib = require('zlib');

const path = process.argv[2];
const buf = fs.readFileSync(path);
const content = buf.toString('binary');

const results = [];

// Find all FlateDecode streams and decompress them
const objRegex = /(\d+ \d+ obj[\s\S]*?endobj)/g;
let objMatch;

while ((objMatch = objRegex.exec(content)) !== null) {
    const obj = objMatch[1];
    
    // Check if this object has a stream with FlateDecode
    const streamMatch = obj.match(/stream\r?\n([\s\S]*?)\r?\nendstream/);
    if (!streamMatch) continue;
    
    const hasFlateDecode = obj.includes('FlateDecode');
    const rawStream = streamMatch[1];
    
    let decoded = '';
    
    if (hasFlateDecode) {
        try {
            // Convert binary string to Buffer
            const streamBuf = Buffer.from(rawStream, 'binary');
            const inflated = zlib.inflateSync(streamBuf);
            decoded = inflated.toString('binary');
        } catch (e) {
            continue;
        }
    } else {
        decoded = rawStream;
    }
    
    // Extract text from PDF operators
    const textParts = [];
    
    // Match Tj: (text) Tj
    const tjRegex = /\(([^)]*)\)\s*Tj/g;
    let m;
    while ((m = tjRegex.exec(decoded)) !== null) {
        textParts.push(m[1]);
    }
    
    // Match TJ arrays: [(text) num (text) ...] TJ
    const tjArrayRegex = /\[((?:[^])*?)\]\s*TJ/g;
    while ((m = tjArrayRegex.exec(decoded)) !== null) {
        const inner = m[1];
        const strRegex = /\(([^)]*)\)/g;
        let sm;
        while ((sm = strRegex.exec(inner)) !== null) {
            textParts.push(sm[1]);
        }
    }
    
    if (textParts.length > 0) {
        // Decode PDF escape sequences
        let text = textParts.join('')
            .replace(/\\n/g, '\n')
            .replace(/\\r/g, '\r')
            .replace(/\\t/g, '\t')
            .replace(/\\\(/g, '(')
            .replace(/\\\)/g, ')')
            .replace(/\\\\/g, '\\');
        results.push(text);
    }
}

const output = results.join('\n');
if (output.trim()) {
    console.log(output);
} else {
    console.log('Could not extract text - PDF may use advanced encoding');
}
