import './App.css';
import {useEffect, useState} from "react";

function App() {
    let normal = {}
    let [ws, setWs] = useState(normal);
    let [session, setSession] = useState(null);

    let [lastMessage, setLastMessage] = useState("")

    let [obj, setObj] = useState({})

    let [image, setImage] = useState("");

    let sendMessage = () => {
        if (ws) {
            let a = JSON.stringify({type: "CONTROL_MESSAGE", data: {verticalSpeed: 1.0, horizontalSpeed: 0.0}})
            console.log(a)
            ws.send(a)
        }
    }

    useEffect(() => {
        let url = 'ws://localhost:8081/cars/1'
        console.log("Execute?")
        let headers = JSON.stringify({
            token: 'My-little-token'
        })
        let websocket = new WebSocket(url)
        websocket.onopen = (wsm, req) => {
            console.log("Connected")
            // websocket.send("COnnnected")
        };
        websocket.onmessage = (e) => {
            setLastMessage("Recived : " + e.data)
            let obj = JSON.parse(e.data)
            if (obj.type === "DISPLAY_MESSAGE") {
                if (obj.data) {
                    if (obj.data.frame) {
                        setImage(obj.data.frame)
                    }
                    if (obj.data) {
                        setObj({
                            lefTime: obj.data.timeToEnd,
                            sessionSteeringId: obj.data.sessionSteeringId,
                            userRentId: obj.data.userRentId
                        })
                    }
                }
            } else if (obj.type === "INFO_MESSAGE") {
                console.log(obj)

                if (obj.data.msg === "CONNECTED_SUCCESSFULLY") {
                    setSession(obj.data.websocketId)
                    console.log("SETTED")
                }
            } else {
                console.log(obj)

            }

        };
        websocket.onclose = (e) => {
            console.log("Disconnected")
        }
        setWs(websocket);
        return () => {
            websocket.close();
        }

    }, [])


    const AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxUVNKakxDWkZQbVVKcFRJQnVVcmM0WUZjR3YwZ0puS080RWJPTXFJc3dnIn0.eyJleHAiOjE2ODQ4ODg2NjYsImlhdCI6MTY4NDg3MDY2NiwianRpIjoiYzM3ZmMyMmQtMTA3NS00MjdmLThhNmUtNWMwODg0NmNjODc3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZmM0ZDM1ZjYtZGM3Ni00NmFhLTgxZDMtZmExNWI5NjVhMDM1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic3ByaW5nYm9vdC1rZXljbG9hay1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMGYyOWNlNWEtOWFjMy00MjRiLThiMzEtYTU2ODY4ZjNlZjIwIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiLCIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJhcHBfdXNlciIsImRlZmF1bHQtcm9sZXMtc3ByaW5nYm9vdGtleWNsb2FrIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImFwcF9hZG1pbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNwcmluZ2Jvb3Qta2V5Y2xvYWstY2xpZW50Ijp7InJvbGVzIjpbImFkbWluIiwidXNlciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMGYyOWNlNWEtOWFjMy00MjRiLThiMzEtYTU2ODY4ZjNlZjIwIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJmaXJzdC1uYW1lMiBsYXN0LW5hbWUzIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcm5hbWUzIiwiZ2l2ZW5fbmFtZSI6ImZpcnN0LW5hbWUyIiwiZmFtaWx5X25hbWUiOiJsYXN0LW5hbWUzIiwiZW1haWwiOiJ0ZXN0NkBlbWFpbC5jb20ifQ.IiyoazFU82RGnJFsnoAv9AQrFhdDgGP8f5POjjelRsbbv0XFFlVy52NTjeioh2eRaxqaScGtRBFB2H5lyc-UIlJLFfSZrOEDyGdjJS9JeB-os-cOJc0IL57UfysgUfCLciIFcIPgryADauk2i_jfmOtthcBjn4RNgcv-W98-1sD7W5O0_2ubvZ-LPAd0yvnqMhXZ9R-MI93CAgV46c_XeMN4bv5fI22K3Oo32Kcf8Ex7fbRTj8iaxIrmhws8J4pIKVckM_XAGZRBt7B0aOV8yNG7r2y9xtGs3pU98qGe8rPk2Prvqgd9U_wmyvxZUJ5BOeOHfkExiw1VIgyPBHa7Jg"

    return (
        <div className="App">
            <header className="App-header">
                <img src={`data:image/jpeg;base64,${image}`} style={{height: "720px", width: "1280px"}} alt="logo"/>
                <button onClick={sendMessage}>Send movement</button>
                <button onClick={() => {
                    if (ws) {
                        console.log(ws.id)
                        fetch("http://localhost:8081/car/rent/1", {
                            method: "POST",
                            headers: {
                                Accept: "application/json",
                                "Content-Type": "application/json",
                                "Authorization": AUTH_TOKEN
                            }
                        }).then(response => {

                            console.log(response.status)

                        }).catch(e => {
                            console.log(e)
                        })
                    }
                }}>RENT car
                </button>
                <button onClick={() => {
                    console.log(ws)
                    if (ws) {
                        console.log(session)
                        fetch("http://localhost:8081/car/take_control/1", {
                            "method": "POST",
                            headers: {
                                Accept: "application/json",
                                "Content-Type": "application/json",
                                "Authorization": AUTH_TOKEN
                            },
                            body: JSON.stringify({
                                "websocketId": session
                            })
                        }).then(response => {

                            console.log(response.status)

                        }).catch(e => {
                            console.log(e)
                        })
                    }
                }}>Take steering
                </button>
                <p>
                    {obj && <h2>LEFT : {obj.lefTime}</h2>}
                    {obj && <h2>RENT BY THIS USER
                        : {obj.userRentId === "fc4d35f6-dc76-46aa-81d3-fa15b965a035" ? "true" : "false"}</h2>}
                    {obj && <h2>Steering by this session : {obj.sessionSteeringId === session ? "true" : "false"}</h2>}
                </p>
            </header>
        </div>
    );
}

export default App;
