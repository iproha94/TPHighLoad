# TPHighLoad
## DZ1 - Web server
Запуск графита:  
sudo service apache2 reload  
sudo service carbon-cache start  

1. Написан на Java Nio.  
2. Работает в 1 поток.  
3. Поддерживает ContentType:  
3.1 text/html  
3.2 text/css  
3.3 text/javascript  
3.4 image/jpeg  
3.5 image/jpeg  
3.6 image/png  
3.7 image/gif  
3.8 application/x-shockwave-flash  
3.9 image/x-icon  
4. С помощью ../ выйти за пределы основной папки не получится.
5. Поддерживаемые методы:  
5.1 GET  
5.2 HEAD  
6. Поддерживаемые ответы:  
6.1 200 OK  
6.2 400 Bad Request  
6.3 403 Forbidden  
6.4 404 Not Found  
6.5 405 Method Not Allowed  
7. Каждую секунду на графит (указывается ip и порт) посылается метрика с rps и загрузкой cpu

## DZ1 - Web client (лабораторка на кафедре)
Открываю tcp, посылаю get запрос, считываю ответ. преобразую в читабельный вид html страничку и вывожу на консоль. 

