import React from 'react';
import { withRouter } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import BackgroundImg from '../commons/images/backgroundLogIn.jpg';
import 'react-datepicker/dist/react-datepicker.css';
import {
    Chart as ChartJS,
    LineElement,
    PointElement,
    LinearScale,
    Title,
    Tooltip,
    Legend,
    CategoryScale,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import { Button, Container, ListGroup, ListGroupItem, Input } from 'reactstrap';
import * as API_CLIENT from './api/client-api';
import * as API_MESSAGES from './api/message-api';

ChartJS.register(LineElement, PointElement, LinearScale, Title, Tooltip, Legend, CategoryScale);

const backgroundStyle = {
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'no-repeat',
    backgroundImage: `url(${BackgroundImg})`,
    width: '100%',
    height: '100vh',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    position: 'relative',
};

const overlayStyle = {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    background: 'rgba(0, 0, 0, 0.7)',
    zIndex: 1,
};

const containerStyle = {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    padding: '40px',
    borderRadius: '15px',
    color: '#333',
    width: '80%',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
    zIndex: 2,
    textAlign: 'center',
};

const buttonStyle = {
    borderRadius: '8px',
    padding: '10px 20px',
    backgroundColor: '#007bff',
    borderColor: '#007bff',
    color: '#fff',
    fontSize: '16px',
    cursor: 'pointer',
    width: '100%',
    marginTop: '20px',
};

const chartStyle = {
    width: '100%',
    height: '500px',
    margin: '20px 0',
};

const chatBoxStyle = {
    width: '100%',
    height: '200px',
    overflowY: 'auto',
    border: '1px solid #ccc',
    padding: '10px',
    marginBottom: '10px',
};
const userMessageStyle = {
    textAlign: 'right',
    backgroundColor: '#007bff',
    color: '#fff',
    borderRadius: '10px',
    padding: '10px',
    margin: '5px 0',
    maxWidth: '80%',
    marginLeft: 'auto',
};
const adminMessageStyle = {
    textAlign: 'left',
    backgroundColor: '#66ff33',
    color: '#000',
    borderRadius: '10px',
    padding: '10px',
    margin: '5px 0',
    maxWidth: '80%',
};
const dotStyle = {
    position: 'absolute',
    bottom: '10px', // Poziționează bulina la 10px distanță de jos
    right: '10px',  // Poziționează bulina la 10px distanță de dreapta
    width: '15px',
    height: '15px',
    backgroundColor: 'green',
    borderRadius: '50%',
};
class Client extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: this.props.match.params.value,
            devices: [],
            selectedDeviceId: '',
            showDevices: false,
            loading: false,
            error: null,
            popupMessage: null,
            showPopup: false,
            chartData: [],
            selectedDate: new Date(),
            chart: false,
            admins: [],
            selectedAdminId: '',
            messages: [],
            messageInput: '',
            showAdmins: false,
            loadingMessage: false,
            typingNotification: '',
            unreadMessages: [],
            seenMessage: false,

        };
        this.websocket = null;
        this.websocketMessage = null;
    }

    componentDidMount() {
        this.initWebSocket();
        this.initWebSocketMessages();
        this.fetchAdmins();
    }

    componentWillUnmount() {
        if (this.websocket) {
            this.websocket.close();
        }
        if (this.websocketMessage) {
            this.websocketMessage.close();
        }
    }

    fetchAdmins = () => {
        API_MESSAGES.getAdmins((result, status, err) => {
            if (status === 200) {
                console.log(result);
                this.setState({ admins: result });
            } else {
                this.setState({ error: err.message });
            }
        });
    };

    initWebSocketMessages = () => {
        const { value } = this.state;
        const chatServerUrl = `ws://localhost/chats/conexiuneClient/${value}`;
        console.log('Connecting to WebSocket chat with clientId:', value);

        this.websocketMessage = new WebSocket(chatServerUrl);

        this.websocketMessage.onopen = () => {
            console.log('Chat WebSocket connected.');

            // Send a message to request unread messages when the connection opens
            const initialMessage = {
                type: 'necitite',  // Type to request unread messages
            };

            this.websocketMessage.send(JSON.stringify(initialMessage)); // Send request message through WebSocket
        };

        this.websocketMessage.onmessage = (event) => {
            try {
                const messageData = JSON.parse(event.data);


                // Procesare notificare typing
                if (messageData.type === 'typing') {
                    const { selectedAdminId } = this.state;

                    if (messageData.dela === selectedAdminId) {
                        this.setState({ typingNotification: 'Admin is typing...' });

                        // Setează un timeout pentru a șterge notificarea după 2 secunde
                        setTimeout(() => {
                            this.setState({ typingNotification: '' });
                        }, 2000);
                    }
                }

                // Procesare mesaje necitite
                else if (messageData.type === 'necitite') {
                    console.log(messageData);

                    // Verificare pentru mesaje necitite
                    if (messageData.necitite && messageData.necitite.length > 0) {
                        console.log('Unread messages aici:', messageData.necitite);
                            // Adaugă mesajele necitite în lista locală
                            this.setState((prevState) => ({
                                unreadMessages: [...prevState.unreadMessages, ...messageData.necitite],
                            }));
                        // Log fiecare mesaj necitit
                        messageData.necitite.forEach((messageId, index) => {
                            console.log(`Unread message ${index + 1}: ${messageId}`);
                        });
                    } else {
                        console.log('No unread messages');
                    }
                } else if (messageData.type === 'mesaje') {
                    console.log(messageData);

                    // Verificare pentru mesaje necitite
                    if (messageData.mesaje && messageData.mesaje.length > 0) {
                        console.log('Unread messages:', messageData.mesaje);

                        // Adaugă mesajele necitite în lista locală
                        this.setState((prevState) => ({
                            messages: [...prevState.messages, { from: 'Admin', content: messageData.mesaje }],
                        }));

                        // Log fiecare mesaj necitit
                        messageData.mesaje.forEach((messageId, index) => {
                            console.log(`Unread message ${index + 1}: ${messageId}`);
                        });
                    }
                }
                else if (messageData.type === 'primite') {
                    console.log(messageData);

                    // Verificare pentru mesaje necitite
                    if (messageData.mesaje && messageData.mesaje.length > 0) {
                        const { selectedAdminId } = this.state;
                        console.log('messages:', messageData.mesaje);
                        console.log('dela:', messageData.dela);
                        console.log('selectat:', selectedAdminId);

                        if(messageData.dela === selectedAdminId)
                        { // Adaugă mesajele necitite în lista locală
                            this.setState((prevState) => ({
                                messages: [...prevState.messages, {from: 'Admin', content: messageData.mesaje}],
                            }));

                            if (this.websocketMessage && this.websocketMessage.readyState === WebSocket.OPEN) {
                                const message = {
                                    type: 'sterge', // Tipul mesajului
                                    sender: selectedAdminId,       // ID-ul adminului selectat
                                };

                                // Trimite mesajul prin WebSocket
                                this.websocketMessage.send(JSON.stringify(message));
                                console.log(`Admin with ID ${selectedAdminId} selected, notification sent.`);
                            } else {
                                console.error('WebSocket is not open. Unable to notify admin selection.');
                            }
                        }
                        else {
                            this.setState((prevState) => ({
                                unreadMessages: [...new Set([...prevState.unreadMessages, messageData.dela])],
                            }));
                            this.renderAdminsList()
                        }

                        // Log fiecare mesaj necitit
                        messageData.mesaje.forEach((messageId, index) => {
                            console.log(`Unread message ${index + 1}: ${messageId}`);
                        });
                    }
                }
                else if (messageData.type === 'group') {
                    console.log(messageData);

                    // Verificare pentru mesaje necitite
                    if (messageData.mesaje && messageData.mesaje.length > 0) {
                        const { selectedAdminId } = this.state;

                        console.log('messages:', messageData.mesaje);
                        console.log('dela:', messageData.dela);
                        console.log('selectat:', selectedAdminId);

                        // Dacă adminul „group” este selectat
                        if (selectedAdminId === 'group') {
                            // Adaugă mesajele de tip „group” în lista locală
                            this.setState((prevState) => ({
                                messages: [...prevState.messages, { from: 'Group', content: messageData.mesaje }],
                            }));

                            if (this.websocketMessage && this.websocketMessage.readyState === WebSocket.OPEN) {
                                const message = {
                                    type: 'sterge', // Tipul mesajului
                                    sender: selectedAdminId, // ID-ul adminului selectat
                                };

                                // Trimite mesajul prin WebSocket
                                this.websocketMessage.send(JSON.stringify(message));
                                console.log(`Group selected, notification sent.`);
                            } else {
                                console.error('WebSocket is not open. Unable to notify group selection.');
                            }
                        } else {
                            // Dacă mesajele „group” sunt primite, dar alt admin este selectat
                            this.setState((prevState) => ({
                                unreadMessages: [...new Set([...prevState.unreadMessages, 'group'])],
                            }));
                            this.renderAdminsList();
                        }

                        // Log fiecare mesaj necitit
                        messageData.mesaje.forEach((messageId, index) => {
                            console.log(`Unread group message ${index + 1}: ${messageId}`);
                        });
                    }
                }

                else if (messageData.type === 'vazut') {

                    this.setState({ seenMessage: true });                }
            } catch (error) {
                console.error('Invalid WebSocket message format:', event.data);
            }
        };



        this.websocketMessage.onerror = (error) => {
            console.error('Chat WebSocket error:', error);
        };

        this.websocketMessage.onclose = () => {
            console.log('Chat WebSocket connection closed. Attempting to reconnect...');
            setTimeout(this.initWebSocketMessages, 5000);  // Reconnect after 5 seconds if the connection is closed
        };
    };



    // Funcția pentru a afișa bulina verde
    renderGreenDot = () => {
        const { seenMessage } = this.state;

        // Dacă mesajul a fost văzut, afișăm bulina verde
        if (seenMessage) {
            return (
                <div style={{
                    width: '10px',
                    height: '10px',
                    borderRadius: '50%',
                    backgroundColor: 'green',
                    marginTop: '10px',  // Adăugăm un spațiu între ultimul mesaj și bulină
                    alignSelf: 'center', // Aliniem bulina la centru
                }}></div>
            );
        }

        // Dacă mesajul nu a fost văzut, nu returnăm nimic
        return null;
    };




    initWebSocket = () => {
        const { value } = this.state;
        const serverUrl = `ws://localhost/measurements/conexiuneClient/${value}`;
        console.log('Connecting to WebSocket with clientId:', value);

        this.websocket = new WebSocket(serverUrl);

        this.websocket.onopen = () => {
            console.log('WebSocket connected for measurements');
        };

        this.websocket.onmessage = (event) => {
            console.log('Message received:', event.data);

            if (event.data === "No data found for the selected device and date") {
                console.log("No data found for the selected device and date");
                this.setState({ chartData: [] });
            } else if (!this.chart) {
                if (this.state.popupMessage !== event.data) {
                    this.setState({ popupMessage: event.data, showPopup: true });
                }
            } else {
                this.chart = false;
                this.parseChartData(event.data);
            }
        };

        this.websocket.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.setState({ showPopup: true, popupMessage: 'WebSocket Error. Retrying...' });
            setTimeout(this.initWebSocket, 5000);
        };

        this.websocket.onclose = () => {
            console.log('WebSocket connection closed');
        };
    };


    handleMessageChange = (event) => {
        this.setState({ messageInput: event.target.value });
    };



    handleTyping = () => {
        const { selectedAdminId } = this.state;
        console.log('typing ...');
        if (this.websocketMessage && this.websocketMessage.readyState === WebSocket.OPEN) {
            console.log('trimite');
            const typingNotification = {
                type: 'typing',
                userId: selectedAdminId,
            };
            this.websocketMessage.send(JSON.stringify(typingNotification));
        }
    };

    renderAdminsList = () => {
        const { admins, selectedAdminId, unreadMessages } = this.state;

        // Adăugăm un admin implicit cu numele "group"
        const adminsWithGroup = [...admins, { id: 'group', username: 'Group' }];

        // Funcția de golire a mesajelor
        const clearMessages = () => {
            this.setState({ messages: [] }); // Exemplu: presupunând că `messages` este lista de mesaje
        };

        return (
            <ListGroup>
                {adminsWithGroup.map((admin) => (
                    <ListGroupItem
                        key={admin.id}
                        onClick={() => {
                            clearMessages(); // Golește lista de mesaje înainte de a selecta adminul
                            this.handleAdminSelection(admin.id); // Apoi selectează adminul
                        }}
                        style={{
                            cursor: 'pointer',
                            backgroundColor: admin.id === selectedAdminId ? '#007bff' : '#fff',
                            color: admin.id === selectedAdminId ? '#fff' : '#000',
                        }}
                    >
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                            <strong>{admin.username}</strong>

                            {/* Blue dot if admin ID is in unreadMessages */}
                            {unreadMessages.includes(admin.id) && (
                                <span
                                    style={{
                                        width: '10px',
                                        height: '10px',
                                        borderRadius: '50%',
                                        backgroundColor: 'blue',
                                        marginLeft: '10px',
                                    }}
                                ></span>
                            )}
                        </div>
                    </ListGroupItem>
                ))}
            </ListGroup>
        );
    };

    renderChatBox = () => {
        const { messages, messageInput, typingNotification, selectedAdminId } = this.state;

        // Verificăm dacă adminul selectat este "group"
        const isGroupSelected = selectedAdminId === 'group';

        return (
            <>
                <div style={chatBoxStyle}>
                    {messages.map((msg, index) => (
                        <div key={index}>
                            {msg.content.map((item, i) => (
                                <div
                                    key={i}
                                    style={{
                                        backgroundColor: 'limegreen',
                                        padding: '10px',
                                        marginBottom: '10px',
                                        borderRadius: '5px',
                                        color: 'black',
                                        ...(msg.from === 'You' ? userMessageStyle : adminMessageStyle),
                                    }}
                                >
                                    {item}
                                </div>
                            ))}
                        </div>
                    ))}
                    {typingNotification && <p><em>{typingNotification}</em></p>}
                    {this.renderGreenDot()}
                </div>
                {/* Ascundem caseta de introducere a mesajului dacă este selectat adminul "group" */}
                {!isGroupSelected && (
                    <>
                        <Input
                            type="text"
                            value={messageInput}
                            onChange={this.handleMessageChange}
                            onKeyUp={this.handleTyping}
                            placeholder="Type your message..."
                        />
                        <Button onClick={this.sendMessageChat} style={{ marginTop: '10px' }}>
                            Send
                        </Button>
                    </>
                )}
            </>
        );
    };





    parseChartData = (message) => {
        const matches = message.match(/Value: \{([\d.]+=[\d\-:\s]+)\}/g);
        if (matches) {
            const newChartData = matches.map((match) => {
                const [, keyValue] = match.match(/Value: \{([\d.]+=[\d\-:\s]+)\}/);
                const [y, x] = keyValue.split('=');
                return { x: x.trim(), y: parseFloat(y.trim()) };
            });

            this.setState((prevState) => ({
                chartData: [...prevState.chartData, ...newChartData],
            }));
        }
    };

    toggleDeviceList = () => {
        const { showDevices } = this.state;
        if (!showDevices) {
            this.fetchDevices();
        }
        this.setState((prevState) => ({
            showDevices: !prevState.showDevices,
        }));
    };

    fetchDevices = () => {
        const { value } = this.state;
        this.setState({ loading: true, error: null });

        API_CLIENT.getDevicesByUserId(value, (result, status, err) => {
            if (status === 200) {
                this.setState({ devices: result, loading: false });
            } else {
                this.setState({ error: err.message, loading: false });
            }
        });
    };

    renderDevices = () => {
        const { devices } = this.state;
        return (
            <ListGroup>
                {devices.map((device) => (
                    <ListGroupItem key={device.id}>
                        <strong>{device.id}</strong> - Description: {device.description}, Address: {device.address}, Maximum Hourly Energy Consumption: {device.maxenergy}
                        {device.user && ` - User: ${device.user.id}`}
                    </ListGroupItem>
                ))}
            </ListGroup>
        );
    };



    handleAdminSelection = (adminId) => {
        this.setState((prevState) => ({
            selectedAdminId: adminId,
            unreadMessages: prevState.unreadMessages.filter((id) => id !== adminId), // Remove adminId from unreadMessages
        }));

        // Verifică dacă websocket-ul este deschis
        if (this.websocketMessage && this.websocketMessage.readyState === WebSocket.OPEN) {
            const message = {
                type: 'adminSelected', // Tipul mesajului
                userId: adminId,       // ID-ul adminului selectat
            };

            // Trimite mesajul prin WebSocket
            this.websocketMessage.send(JSON.stringify(message));
            console.log(`Admin with ID ${adminId} selected, notification sent.`);
        } else {
            console.error('WebSocket is not open. Unable to notify admin selection.');
        }
    };


    handleDeviceChange = (event) => {
        this.setState({ selectedDeviceId: event.target.value });
    };

    sendChartRequest = () => {
        const { selectedDate, selectedDeviceId } = this.state;

        if (!selectedDeviceId) {
            this.setState({ popupMessage: 'Please select a device!', showPopup: true });
            return;
        }

        this.chart = true;
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            const formattedDate = selectedDate.toISOString().split('T')[0];
            const message = `view-chart:${selectedDeviceId}:${formattedDate}`;
            this.websocket.send(message);
        }
    };

    renderDevicesDropdown = () => {
        const { devices } = this.state;
        return (
            <select onChange={this.handleDeviceChange} value={this.state.selectedDeviceId}>
                <option value="" disabled>
                    Select a device
                </option>
                {devices.map((device) => (
                    <option key={device.id} value={device.id}>
                        {`Device ${device.id}`}
                    </option>
                ))}
            </select>
        );
    };

    sendMessageChat = () => {
        const { messageInput, selectedAdminId } = this.state;
        const { seenMessage } = this.state;

        if (!selectedAdminId) {
            alert('Please select an admin!');
            return;
        }

        if (messageInput.trim() === '') {
            return;
        }

        const message = {
            type: 'message',
            userId: selectedAdminId,
            content: [messageInput],
            from: 'You', // Marca mesajul ca fiind de la utilizator
        };

        // Adăugăm mesajul trimis de utilizator în lista de mesaje
        this.setState((prevState) => ({
            messages: [...prevState.messages, message],
            messageInput: '', // Golim inputul după trimitere
            seenMessage: false
        }));

        if (this.websocketMessage) {
            console.log('WebSocket readyState:', this.websocketMessage.readyState);
            if (this.websocketMessage.readyState === WebSocket.OPEN) {
                this.websocketMessage.send(JSON.stringify(message)); // Trimitem mesajul prin WebSocket
            } else {
                console.error('WebSocket is not open. readyState:', this.websocketMessage.readyState);
                alert('WebSocket is not open, please try again later.');
            }
        } else {
            console.error('WebSocket object is null.');
        }
    };


    handleDateChange = (date) => {
        this.setState({ selectedDate: date });
    };

    renderPopup = () => {
        const { popupMessage, showPopup } = this.state;
        if (!showPopup) return null;

        const popupStyle = {
            position: 'fixed',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            zIndex: 1000,
            backgroundColor: '#fff',
            padding: '20px',
            borderRadius: '10px',
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
            textAlign: 'center',
        };
        const overlayStyle = {
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            zIndex: 999,
        };

        return (
            <>
                <div style={overlayStyle} onClick={this.closePopup}></div>
                <div style={popupStyle}>
                    <h3>Notification</h3>
                    <p>{popupMessage}</p>
                    <Button style={buttonStyle} onClick={this.closePopup}>
                        Close
                    </Button>
                </div>
            </>
        );
    };

    closePopup = () => {
        this.setState({ showPopup: false });
    };

    renderChart = () => {
        const { chartData } = this.state;

        if (chartData.length === 0) {
            console.log('No chart data available');
            return <p>No data available for the chart.</p>;
        }

        console.log('Chart Data:', chartData);

        const data = {
            labels: chartData.map((dataPoint) => dataPoint.x),
            datasets: [
                {
                    label: 'Energy Consumption',
                    data: chartData.map((dataPoint) => dataPoint.y),
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0, 123, 255, 0.2)',
                    fill: true,
                },
            ],
        };

        const options = {
            responsive: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                },
                title: {
                    display: true,
                    text: 'Energy Consumption Over Time',
                },
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: 'Timestamp',
                    },
                },
                y: {
                    title: {
                        display: true,
                        text: 'Value',
                    },
                },
            },
        };

        return <div style={chartStyle}><Line data={data} options={options} /></div>;
    };

    render() {
        const { showDevices, loading, error, selectedDate } = this.state;

        return (
            <div style={backgroundStyle}>
                <div style={overlayStyle}></div>
                <Container style={containerStyle}>
                    <h2>Your Devices</h2>
                    <Button style={buttonStyle} onClick={this.toggleDeviceList}>
                        {showDevices ? 'Hide Devices' : 'Show Devices'}
                    </Button>
                    {loading && <p>Loading...</p>}
                    {error && <p style={{ color: 'red' }}>Error: {error}</p>}
                    {showDevices && !loading && !error && this.renderDevices()}

                    <h3>Chart</h3>
                    <p>Select a device to view the chart:</p>
                    {this.renderDevicesDropdown()}
                    <p>Select a date to view the chart:</p>
                    <DatePicker selected={selectedDate} onChange={this.handleDateChange} />
                    <Button style={buttonStyle} onClick={this.sendChartRequest}>
                        Request Chart Data
                    </Button>
                    {this.renderChart()}

                    <h4>Admins</h4>
                    {this.renderAdminsList()}

                    <h5>Chat</h5>
                    {this.renderChatBox()}
                </Container>
                {this.renderPopup()}
            </div>
        );
    }
}

export default withRouter(Client);
