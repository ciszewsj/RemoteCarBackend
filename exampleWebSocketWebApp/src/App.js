import './App.css';
import {useEffect, useState} from "react";

function App() {
    let normal = {}
    let [ws, setWs] = useState(normal);

    let [lastMessage, setLastMessage] = useState("")

    let [image, setImage] = useState("");

    let sendMessage = () => {
        if (ws) {
            let a = JSON.stringify({type: "CONTROL_MESSAGE", data: {verticalSpeed: 1.0, horizontalSpeed: 1.0}})
            console.log(a)
            ws.send(a)
        }
    }

    useEffect(() => {
        let url = 'ws://localhost:8081/cars/1'
        console.log("Execute?")
        let websocket = new WebSocket(url)
        websocket.onopen = () => {
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
                <button onClick={sendMessage}>Button to send !
                </button>
                <p>
                    {ws.open && <h2>DZIALA</h2>}
                    {!ws.open && <h2>NIE DZIALA</h2>}
                </p>
            </header>
        </div>
    );
}

export default App;
