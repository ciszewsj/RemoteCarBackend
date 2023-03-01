import './App.css';
import {useEffect, useState} from "react";

function App() {
    let normal = {}
    let [ws, setWs] = useState(normal);

    let [lastMessage, setLastMessage] = useState("")

    let [image, setImage] = useState("");

    useEffect(() => {
        let url = 'ws://localhost:8080/echo/'
        console.log("Execute?")
        let websocket = new WebSocket(url)
        websocket.onopen = () => {
            console.log("Connected")
            // websocket.send("COnnnected")
        };
        websocket.onmessage = (e) => {
            console.log("Recived : " + e.data);
            setLastMessage("Recived : " + e.data)
            let obj = JSON.parse(e.data)
            if (obj.image) {
                setImage(obj.image)
            }
            console.log(obj)
        };
        setWs(websocket);
        return () => {
            websocket.close();
        }

    }, [])

    return (
        <div className="App">
            <header className="App-header">
                <img src={`data:image/jpeg;base64,${image}`} style={{height: "80px", width: "80px"}} alt="logo"/>
                <button onClick={() => {
                    try {

                        ws.send(JSON.stringify({type: "CONTROL_MESSAGE", data: {}}))
                        console.log("Send message")
                    } catch (e) {
                        console.log("NOT READY")
                    }
                }}>Button to send !
                </button>
                <p>
                    Edit <code>src/App.js</code> and save to reload.
                </p>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
                <p>
                    {lastMessage}
                </p>
            </header>
        </div>
    );
}

export default App;
