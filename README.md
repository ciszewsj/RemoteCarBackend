# RemoteCarBackend

Swagger - http://localhost:8080/swagger-ui/index.html#/

# Test

1. Start python websocket server and java backend
2. Go to swagger and send : POST /car_admin with body:

   {
   "name": "string",
   "url": "ws://localhost:8000/",
   "fps": 0 }

3. Send POST /car/admin/start/1
4. Start react webapp