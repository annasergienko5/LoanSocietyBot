
echo $1
echo $2

curl -v --location --request POST 'https://api.telegram.org/$1/sendMessage' \
--header 'Content-Type: application/json' \
--data-raw '{
    "chat_id": "$CHAT_ADMIN_ID",
    "text": "Обновились --test",
    "disable_notification": true
}'

#curl --location --request POST 'https://api.telegram.org/$KEY/sendMessage' \
#--header 'Content-Type: application/json' \
#--data-raw '{
#    "chat_id": "$CHATID",
#    "text": "Обновились",
#    "disable_notification": true
#}'