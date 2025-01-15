from transformers import BertTokenizer, BertModel
import torch
import sys
import numpy as np

def get_cls_token_for_long_text(text, max_length=512, stride=256):
    # 初始化tokenizer和模型
    tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
    model = BertModel.from_pretrained('bert-base-chinese')
    
    # 将文本分成多个片段
    tokens = tokenizer.tokenize(text)
    all_cls_tokens = []
    
    # 使用滑动窗口处理长文本
    for i in range(0, len(tokens), stride):
        # 获取当前片段
        chunk_tokens = tokens[i:i + max_length]
        if len(chunk_tokens) < 50:  # 忽略太短的片段
            continue
            
        # 转换回文本
        chunk_text = tokenizer.convert_tokens_to_string(chunk_tokens)
        
        # 处理当前片段
        inputs = tokenizer(chunk_text, 
                          return_tensors="pt",
                          max_length=max_length,
                          truncation=True,
                          padding=True)
        
        # 获取模型输出
        with torch.no_grad():  # 不计算梯度，节省内存
            outputs = model(**inputs)
            cls_token = outputs.last_hidden_state[0][0].numpy()
            all_cls_tokens.append(cls_token)
    
    # 如果没有获取到任何向量，处理整个文本的前max_length个token
    if not all_cls_tokens:
        inputs = tokenizer(text, 
                          return_tensors="pt",
                          max_length=max_length,
                          truncation=True,
                          padding=True)
        with torch.no_grad():
            outputs = model(**inputs)
            cls_token = outputs.last_hidden_state[0][0].numpy()
            return cls_token
    
    # 计算所有CLS token的平均值
    return np.mean(all_cls_tokens, axis=0)

def main():
    if len(sys.argv) != 3:
        print("Usage: python getClsToken.py input_file output_file")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    # 读取输入文件
    with open(input_file, 'r', encoding='utf-8') as f:
        text = f.read()

    # 获取CLS token
    cls_token = get_cls_token_for_long_text(text)

    # 保存结果
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(str(cls_token.tolist()))

if __name__ == "__main__":
    main()