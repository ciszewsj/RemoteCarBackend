import './App.css';
import {useEffect, useState} from "react";
import jwt_decode from 'jwt-decode'

function App() {
    let normal = {}
    let [ws, setWs] = useState(normal);
    let [session, setSession] = useState(null);
    let [lastMessage, setLastMessage] = useState("")
    let [obj, setObj] = useState({})
    let [image, setImage] = useState("");

    let [userId, setUserId] = useState("")

    const [AUTH_TOKEN, setAUTH_TOKEN] = useState("")


    let sendMessage = () => {
        if (ws) {
            let a = JSON.stringify({type: "CONTROL_MESSAGE", data: {verticalSpeed: 1.0, horizontalSpeed: 0.0}})
            console.log(a)
            ws.send(a)
        }
    }

    useEffect(() => {
        let url = 'ws://localhost/api/cars/1'
        console.log("Execute?")
        let headers = JSON.stringify({
            token: 'My-little-token'
        })
        let websocket = new WebSocket(url)
        websocket.onopen = (wsm, req) => {
            console.log("Connected")
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


    return (
        <div className="App">
            <header className="App-header">
                <img src={`data:image/jpeg;base64,${image}`} style={{height: "720px", width: "1280px"}} alt="logo"/>
                <button onClick={() => {
                    const formData = new URLSearchParams();
                    formData.append('client_id', "springboot-keycloak-client");
                    formData.append('username', 'admin');
                    formData.append('password', 'admin');
                    formData.append('grant_type', 'password');

                    fetch("http://localhost/auth/realms/SpringBootKeycloak/protocol/openid-connect/token", {
                        method: "POST",
                        headers: {
                            Accept: "application/json",
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        body: formData
                    }).then(
                        response => {
                            console.log(response.status)
                            if (response.status === 200) {
                                response.json().then(json => {
                                    console.log(json)
                                    setAUTH_TOKEN(json.access_token)
                                    let decodedJwt = jwt_decode(json.access_token)
                                    console.log(decodedJwt)
                                    setUserId(decodedJwt.sub)
                                })
                            }
                        }
                    )
                }}>Login
                </button>
                <button onClick={sendMessage}>Send movement</button>
                <button onClick={() => {
                    if (ws) {
                        console.log(ws.id)
                        fetch("http://localhost/api/car/rent/1", {
                            method: "POST",
                            headers: {
                                Accept: "application/json",
                                "Content-Type": "application/json",
                                "Authorization": "Bearer " + AUTH_TOKEN
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
                        fetch("http://localhost/api/car/take_control/1", {
                            "method": "POST",
                            headers: {
                                Accept: "application/json",
                                "Content-Type": "application/json",
                                "Authorization": "Bearer " + AUTH_TOKEN
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
                <button onClick={() => {
                    console.log(ws)
                    if (ws) {
                        ws.send(JSON.stringify({type: "CONFIG_MESSAGE", data: {size: "P144"}}))
                    }
                }}>Change resolution 144
                </button>
                <button onClick={() => {
                    console.log(ws)
                    if (ws) {
                        ws.send(JSON.stringify({type: "CONFIG_MESSAGE", data: {size: "P240"}}))
                    }
                }}>Change resolution 240
                </button>
                <button onClick={() => {
                    console.log(ws)
                    if (ws) {
                        ws.send(JSON.stringify({type: "CONFIG_MESSAGE", data: {size: "P360"}}))
                    }
                }}>Change resolution 360
                </button>
                <button onClick={() => {
                    console.log(ws)
                    if (ws) {
                        ws.send(JSON.stringify({type: "CONFIG_MESSAGE", data: {size: "P480"}}))
                    }
                }}>Change resolution 480
                </button>
                <button onClick={() => {
                    console.log(ws)
                    if (ws) {
                        ws.send(JSON.stringify({type: "CONFIG_MESSAGE", data: {size: "P720"}}))
                    }
                }}>Change resolution 720
                </button>
                <p>
                    {obj && <h2>LEFT : {obj.lefTime}</h2>}
                    {obj && <h2>RENT BY THIS USER
                        : {obj.userRentId === userId ? "true" : "false"}</h2>}
                    {obj && <h2>Steering by this session : {obj.sessionSteeringId === session ? "true" : "false"}</h2>}
                </p>
            </header>
        </div>
    );
}

export default App;
