#!/bin/bash
# 推送到 GitHub 脚本

echo "========================================"
echo "推送到 GitHub"
echo "========================================"
echo ""

# 检查 SSH key
if [ ! -f ~/.ssh/id_ed25519 ]; then
    echo "生成 SSH key..."
    ssh-keygen -t ed25519 -C "3093453583@qq.com" -f ~/.ssh/id_ed25519 -N ""
    echo ""
    echo "请将以下公钥添加到 GitHub:"
    echo "https://github.com/settings/keys"
    echo ""
    cat ~/.ssh/id_ed25519.pub
    echo ""
    echo "添加后按回车继续..."
    read
fi

# 添加 GitHub 到 known_hosts
ssh-keyscan -t ed25519 github.com >> ~/.ssh/known_hosts 2>/dev/null

# 切换到 SSH 协议
echo "配置远程仓库..."
git remote set-url github git@github.com:ShangCodeHub/blog.git

# 测试连接
echo "测试连接..."
ssh -T git@github.com

# 推送
echo ""
echo "推送到 GitHub..."
git push github master

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 推送成功！"
    echo "仓库地址: https://github.com/ShangCodeHub/blog"
else
    echo ""
    echo "❌ 推送失败，请检查错误信息"
fi
