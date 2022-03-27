簡單普通的Discord 機器人，基於[JDA](https://github.com/DV8FromTheWorld/JDA)開發。
# 機器人設定
先開啟[config.json](https://github.com/Huanying04/DiscordRobot/blob/master/config.json)文件設定以下參數: 
* **TOKEN**  
機器人TOKEN。設定成你的機器人Token。不知道在哪裡的進入[Discord Developer Portal](https://discord.com/developers/applications)選擇你的機器人然後左邊有個**Bot**進去之後就能看到Token了。
* **OWNER**  
機器人的持有者。就是你自己的ID。
* **PIXIV_PHPSESSID**  
pixiv的PHPSESSID，在cookie裡，自己去找。因為做不出登入所以就用PHPSESSID代替啦~(喂

  **注意：**PHPSESSID會**因為登出而失效**，如果登出了就拿新的代替。
* **SAUCENAO_KEY**  
SauceNAO的用戶API KEY，用於搜尋圖片

然後將打包好的jar以cmd開啟，並且在同個資料夾放進剛剛改過的config.json。或者直接在IDE裡執行。

# 功能
* 隨機pixiv圖片
* 搜尋動漫圖片(SuaceNAO)
* 使用pixiv作品ID顯示作品
* 搜尋pixiv插畫

# 主要功能
## 指定pixiv作品
指令格式: `s!pixiv [插畫id] <頁碼，可選>`

參數:

|  參數  |  說明  |
|-------|--------|
| 插畫id |pixiv插畫的id。|
| 頁碼 |分享多頁插畫時可選的參數。頁碼預設為0，並從0開始數。若超過插畫最大頁數則自動歸正為0。|

![pixiv](https://raw.github.com/Huanying04/Nekobot/master/images/Pixiv.PNG)

## 偵測pixiv插畫網址
機器人會自動偵測每條新訊息中是否含有pixiv插畫的網址並將圖片發出來。

![pixiv](https://raw.github.com/Huanying04/Nekobot/master/images/PixivURL.PNG)

## 隨機pixiv作品
指令格式: `s!a`

別名: `s!i`, `s!anime`

![a](https://raw.githubusercontent.com/Huanying04/Nekobot/master/images/RandomAnimeIllustration.PNG)
## 以圖搜圖(動漫圖)
指令格式: `s!sauce [圖片]`

參數:

圖片必須以「附件」或「回覆含有圖片的訊息」出現。

![sauce](https://raw.github.com/Huanying04/Nekobot/master/images/Sauce.PNG)
## 搜尋pixiv插畫
指令格式: `s!search [關鍵字]`

![search](https://raw.github.com/Huanying04/Nekobot/master/images/Search.PNG)
