# 贡献代码指南

在向 Slimefun 汉化版提交代码前，你必须先阅读这个指南。

# 提交信息规范

本项目强制使用 [约定式提交](https://www.conventionalcommits.org/zh-hans/v1.0.0/) 的提交信息规范。

> 简单来说, 你的提交信息需要包含以下内容:
> 
> <类型>[可选 范围]: <描述>
> 
> 例如一个添加了新功能的提交应为 feat(item): add new item to Slimefun

此外, 推荐你使用 [gitmoji](https://gitmoji.dev/) 规范，但并不强制你使用。

如果你提交的代码中解决或处理了 Issue 中的问题，请你在主提交消息外显式声明。

> 如 resolves #114514, fix #114514 等

如果是修复请在主提交消息上声明，不必重复声明。

另外的, 如果是与翻译相关的提交，类型应为 trans。

# 代码规范

**!! 本项目使用空格缩进 !!**

请不要过度缩减代码长度, 空格少了 Slimefun 也不会因此跑得更快.

如果你希望你的代码被合并至官方, 请遵守 Slimefun 主仓库的 [提交规范](https://github.com/Slimefun/Slimefun4/blob/master/CONTRIBUTING.md)

# 提交代码类型
你提交的代码可以是修复、新增内容和 API。

下游代码现在支持提交 API 相关代码，开发者们可以通过 jitpack 依赖汉化版的 Slimefun。