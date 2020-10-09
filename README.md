# DiscordRobot
Discord 機器人
## 機器人設定
先開啟[config.json](https://github.com/Huanying04/DiscordRobot/blob/master/config.json "config.json")文件設定以下參數: 
* **TOKEN**  
機器人TOKEN。設定成你的機器人Token。不知道在哪裡的進入[Discord Application](https://discord.com/developers/applications "Discord Application")選擇你的機器人然後左邊有個**Bot**進去之後就能看到Token了。
* **OWNER**  
機器人的持有者。就是你自己的ID。
* **PIXIV_PHPSESSID**  
pixiv的PHPSESSID。因為做不出登入所以就用PHPSESSID代替啦~(喂
* **PIXIV_UPDATE_CHECKER**  
是否開啟pixiv用戶更新推播
* **MINECRAFT_UPDATE_CHECKER**  
是否開啟Minecraft更新推播
* **PIXIV_PUSH_NOTIFICATION_CHANNEL**  
pixiv用戶更新推播頻道
* **PIXIV_R18_PUSH_NOTIFICATION_CHANNEL**  
pixiv用戶更新推播頻道(R18)
* **MINECRAFT_PUSH_NOTIFICATION_CHANNEL**  
Minecraft更新推播頻道

然後將打包好的jar以cmd開啟，或者直接在編譯器裡執行。
## 使用方法
### 指令
使用方法：<指令前綴(預設為!)><指令名稱> <參數> 
#### hi
回覆"你好啊"

![hi示例](https://i.imgur.com/JkklLvv.png "示例")
#### anime
別稱: illustration、i、a

格式: <指令前綴><指令名稱 | 指令別稱>

從[config.json](https://github.com/Huanying04/DiscordRobot/blob/master/config.json "config.json")文件中的**RANDOM_PIXIV_SEARCH_KEYWORDS**隨機挑選關鍵字搜尋返回隨機作品圖片

![anime示例](https://i.imgur.com/McJhld3.png "示例")
#### meow
隨機發送"喵"

![meow示例](https://i.imgur.com/VXuB6o0.png "示例")
#### pixiv
格式: <指令前綴><指令名稱> <作品id>

發送pixiv作品第一頁及作品資訊

![pixiv示例](https://i.imgur.com/XCQAHfX.png "示例")
#### saucenao
使用**sauceNAO**搜尋圖片
發圖片時在註解內輸入 <指令前綴><指令名稱> 使用

![saucenao示例](https://i.imgur.com/5RsHTVv.png "示例")
#### setting
（待補充）
#### shutdown
關閉機器人
