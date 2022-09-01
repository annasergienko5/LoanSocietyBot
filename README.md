# GringottsTool



## Запуск

Зарегестрируйтесь и установите [ngrok](https://dashboard.ngrok.com/get-started/setup)

или выполните команду:

* sudo snap install ngrok

а потом зарегистрируйтесь, перейдите на вкладку установки, выполните в консоли команду из раздела: 2. Connect your account
Пример: 

* ngrok config add-authtoken 2BJ56h4...c455f

запустите ngrok командой:

* ngrok http 1234 - где "1234" это порт. Можете выбрать любой

Скопируйте сгенерированный url адрес напротив "Forwarding". Пример: https://1c4f-185-185-121-238.eu.ngrok.io
Это и будет вашим WEBHOOK_PATH.

Регистрируйте вебхук в Telegram, формируя в строке браузера запрос вида:

… и запускаем приложение в своей IDE!

Переменные окружения при запуске:
* WEBHOOK_PATH - url сгенерированный ngrok или другой адрес сервера который будет ловить обновления
* TOKEN_BOT - токен бота
* BOT_USERNAME - имя бота
* PORT - порт, который вы задали при запуске ngrok или другой локальный порт на который будут приходить обновления с сервера
* APPLICATION_NAME - имя приложения (пример: Google Sheet Service)
* CREDENTIALS_FILE_PATH - полный путь к файлу  json для подключения к google api (файл может лежать в любой директории) 
Пример: "CREDENTIALS_FILE_PATH=/home/user/Projects/gringottstool/token/Web Client 4.json"
* SHEET_ID - id таблички из url (пример: https://docs.google.com/spreadsheets/d/1234abcd/edit#gid=1825291932 "1234abcd" - это id)
* RULE - ссылка на правила
* PUBLIC_CHAT_ID - идентификационный номер телеграмм общего чата ПОЛЬЗОВАТЕЛЕЙ приложения. Например:"PUBLIC_CHAT_ID=-730818881"
* ADMIN_CHAT_ID - идентификационный номер телеграмм общего чата АДМИНОВ приложения. Например:"ADMIN_CHAT_ID=-730818881"
* DEBT_REMINDER_TIME - переменная в выражении определяющем время отправки сообщения оповещающем о должниках в общий чат и чат админов.
  Выражение использует формат cron.
  Для оповещения раз в месяц первого числа в 10 утра:"DEBT_REMINDER_TIME=0 0 10 1 * ?"
  Для оповещения раз в минуту: "DEBT_REMINDER_TIME=0 * * ? * *"
* CRON_TIMEZONE - временная зона в которой ведется вычисления времени отправки сообщения оповещающем о должниках в общий чат.
  Для выбора зоны +3:"CRON_TIMEZONE=Europe/Moscow". Для правильных вычислений временная зона в БД и в этой переменной должны совпадать.
* PROXY - переменная содержит url-адрес ссылки на прокси сервер. Пример:"PROXY=https://t.me/proxy?server....."
* TODAY_PAYERS_REMINDER_TIME - переменная в выражении определяющем время отправки сообщения оповещающем о должниках, 
у которых сегодня - последний день до фиксации просрочки. в общий чат и чат админов. Выражение использует формат cron.
Пример раз в минуту:"TODAY_PAYERS_REMINDER_TIME=0 * * ? * *"
Пример раз в день в 9 утра:"TODAY_PAYERS_REMINDER_TIME=0 0 9 * * ?"