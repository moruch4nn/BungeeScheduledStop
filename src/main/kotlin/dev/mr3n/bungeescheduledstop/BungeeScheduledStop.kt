package dev.mr3n.bungeescheduledstop

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.time.LocalDateTime
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class BungeeScheduledStop: Plugin() {

    override fun onEnable() {
        // config.ymlの内容をロード
        val config = this.loadConfig("config.yml")
        // 再起動する時刻(時)を取得
        val hour = config.getInt("at.hour", 5)
        // 再起動する時刻(分)を取得
        val minute = config.get("at.minute", 0)
        // ループ用のタイマーを作成
        val timer = Timer()
        // 再起動時のメッセージ
        val kickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.kick"))
        // 30秒おきにループ
        timer.scheduleAtFixedRate(1000 * 30,1000 * 30) {
            // 現在時刻
            val now = LocalDateTime.now()
            if(now.hour == hour && now.minute == minute) {
                // if:時間と分が一致した場合はサーバーを再起動
                // すべてのプレイヤーを切断する
                proxy.players.forEach { player -> player.disconnect(*TextComponent.fromLegacyText(kickMessage)) }
                // サーバー終了(docker compose の restart の項目を always に設定していない場合普通に終了して終わります)
                proxy.stop(kickMessage)
            }
        }
        config.save("config.yml")
    }

    fun Configuration.save(name: String) {
        // プラグインのフォルダがない場合は作成
        if(!dataFolder.exists()) { dataFolder.mkdir() }
        ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(this, dataFolder.resolve(name))
    }
    fun Plugin.loadConfig(name: String): Configuration {
        // プラグインのフォルダがない場合は作成
        if(!dataFolder.exists()) { dataFolder.mkdir() }
        // デフォルトのconfigがない場合は作成
        if(!dataFolder.resolve(name).exists()) { getResourceAsStream(name).transferTo(dataFolder.resolve(name).outputStream()) }
        return ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(dataFolder.resolve(name))
    }
}