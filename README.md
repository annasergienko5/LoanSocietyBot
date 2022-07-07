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
* WEBHOOK_PATH - url сгенерированный ngrok
* TOKEN_BOT - токен бота
* BOT_USERNAME - имя бота
* PORT - порт, который вы задали при запуске ngrok
* APPLICATION_NAME - имя приложения (пример: Google Sheet Service)
* CREDENTIALS_FILE_PATH - путь к файлу json для подключения к google api (файл должен лежать в resource)
* SHEET_ID - id таблички из url 
* RULE - ссылка на правила
(пример: https://docs.google.com/spreadsheets/d/1234abcd/edit#gid=1825291932
  1234abcd - это id)
