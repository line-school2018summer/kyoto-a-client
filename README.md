# Android Application for SHABEL - Realime Messaging Application

## 概要
SHABELはLine SUMMER INTERNSHIP 2018 エンジニアスクールコースにてKyoto Aチームによって開発されたリアルタイムメッセージングアプリです。

このレポジトリではSHABELにおけるAndroidクライアントアプリを扱っています。
サーバーサイドアプリケーションのレポジトリは[こちら](https://github.com/line-school2018summer/kyoto-a-api)からアクセスできます。

## 開発環境
- Kotlin
- Android Studio

## 実装機能

### STOMP over Websocket
SHABELではリアルタイムでのメッセージのやり取りをSTOMP over Websocketを用いて実現しています。
STOMPはPub - Sub型のメッセージングプロトコルであり、TCP上、Websocket上で利用できますす。今回はWebsocket上での実装を行いました。

### REST API
リアルタイム以外で情報を受け取る時はSTOMP上ではなくREST APIを叩いて取得するようになっています。


### APIリファレンス
[こちら](https://kyoto-a-api.pinfort.me/swagger-ui.html)からSwagger UI形式のAPIリファレンスを見ることができます。

https://kyoto-a-api.pinfort.me/swagger-ui.html
