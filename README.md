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

Переменные окружения при запуске:
* WEBHOOK_PATH - url сгенерированный ngrok
* TOKEN_BOT - токен бота
* BOT_USERNAME - имя бота
* PORT - порт, который вы задали при запуске ngrok
