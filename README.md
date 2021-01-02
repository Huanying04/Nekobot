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
# 功能
* 隨機pixiv圖片
* 搜尋動漫圖片(SuaceNAO)
* 使用pixiv作品ID顯示作品
* 搜尋pixiv插畫
* pixiv畫師更新推播
* Minecraft更新推播