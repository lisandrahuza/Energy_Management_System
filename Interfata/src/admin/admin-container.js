import React from 'react';
import { withRouter } from 'react-router-dom';
import {Button, Container, Input, ListGroup, ListGroupItem} from 'reactstrap';
import * as API_ADMIN from './api/admin-api';  // Import the new API functions
import BackgroundImg from '../commons/images/backgroundLogIn.jpg';
import * as API_LOGIN from "../logIn/api/login-api";

const backgroundStyle = {
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'no-repeat',
    width: "100%",
    height: "100vh",
    backgroundImage: `url(${BackgroundImg})`,
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
    background: 'rgba(0, 0, 0, 0.7)',  // Dark overlay for contrast
    zIndex: 1,
};

const containerStyle = {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    padding: '40px',
    borderRadius: '15px',
    color: '#333',
    width: '2000px',
    display: 'flex',
    justifyContent: 'space-between',
    position: 'relative',
    zIndex: 2,
};

const leftSectionStyle = {
    width: '50%',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'flex-start',
};

const formContainerStyle = {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    padding: '30px',
    borderRadius: '10px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    color: '#333',
    width: '40%',
    alignSelf: 'flex-start',
};
const bottomRightStyle = {
    position: 'absolute', // Poziționare relativă la containerul părinte
    right: '20px',        // Distanța față de marginea din dreapta
    bottom: '20px',       // Distanța față de marginea de jos
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    padding: '15px',      // Reducem paddingul pentru a face stilul general mai compact
    borderRadius: '10px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
    width: '500px',       // Mai lat
    height: '500px',      // Mai scurt
    zIndex: 10,           // Asigură-te că este deasupra altor elemente
};


const chatBoxStyle = {
    width: '500px',       // Mai lat
    height: '150px',      // Mai scurt
    overflowY: 'auto',
    border: '1px solid #ccc',
    padding: '10px',
    marginBottom: '10px',
};


const buttonStyle = {
    borderRadius: '8px',
    padding: '10px 20px',
    backgroundColor: '#007bff',
    borderColor: '#007bff',
    color: '#fff',
    fontSize: '16px',
    cursor: 'pointer',
    marginBottom: '20px',
    width: '100%',
};

const userMessageStyle = {
    textAlign: 'left',
    backgroundColor: '#66ff33',
    color: '#000',
    borderRadius: '10px',
    padding: '10px',
    margin: '5px 0',
    maxWidth: '80%',

};
const adminMessageStyle = {
    textAlign: 'right',
    backgroundColor: '#007bff',
    color: '#fff',
    borderRadius: '10px',
    padding: '10px',
    margin: '5px 0',
    maxWidth: '80%',
    marginLeft: 'auto',
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
class Admin extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: this.props.match.params.value,
            activeSection: null,
            devices: [],
            users: [],
            mappings: [],
            showUsers: false,
            showDevices: false,
            showDeviceUpdateForm: false,
            showDeviceCreateForm: false,
            showDeviceDeleteForm: false,
            showUserUpdateForm: false,
            showUserCreateForm: false,
            showUserDeleteForm: false,
            showCreateMappingForm: false,
            showDeleteMappingForm: false,
            loadingUsers: false,
            loadingDevices: false,
            errorUsers: null,
            errorDevices: null,
            updatedDevice: {
                id: null,
                description: '',
                address: '',
                maxPower: ''
            },
            createDevice: {
                description: '',
                address: '',
                maxPower: ''
            },
            deleteDevice: {
                id:null
            },
            updatedUser: {
                id:null,
                username: '',
                name: '',
                isAdmin: false,
                password: ''
            },
            createUser: {
                username: '',
                name: '',
                isAdmin: false,
                password: ''
            },
            deleteUser: {
                username: ''
            },
            createMapping: {
                id_device: '',
                id_user:''
            },
            deleteMapping: {
                id_device: ''
            },
            usersMessages: [],
            usersGroup: [],
            selectedUserId: '',
            selectedUserIds: [],
            messages: [],
            messagesGroup: [],
            messageInput: '',
            messageInputGroup: '',
            showUsersMessage: false,
            loadingMessage: false,
            typingNotification: '',
            unreadMessages: [],
            seenMessage: false,
        };
        this.websocketMessage = null;
    }
    componentDidMount() {
        this.initWebSocketMessages();
        this.fetchUsersMessages();
        this.fetchUsersGroup();
    }
    componentWillUnmount() {
        if (this.websocketMessage) {
            this.websocketMessage.close();
        }
    }
    fetchUsersMessages = () => {
        API_ADMIN.getUsers((result, status, err) => {
            if (status === 200) {
                console.log(result);
                this.setState({ usersMessages: result });
            } else {
                this.setState({ error: err.message });
            }
        });
    };

    fetchUsersGroup = () => {
        API_ADMIN.getUsers((result, status, err) => {
            if (status === 200) {
                console.log(result);
                this.setState({ usersGroup: result });
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
                    const { selectedUserId } = this.state;

                    if (messageData.dela === selectedUserId) {
                        this.setState({ typingNotification: 'User is typing...' });

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
                        console.log('Unread messages:', messageData.necitite);

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
                            messages: [...prevState.messages, {from: 'Admin', content: messageData.mesaje}],
                        }));

                        // Log fiecare mesaj necitit
                        messageData.mesaje.forEach((messageId, index) => {
                            console.log(`Unread message ${index + 1}: ${messageId}`);
                        });
                    }
                } else if (messageData.type === 'primite') {
                    console.log(messageData);

                    // Verificare pentru mesaje necitite
                    if (messageData.mesaje && messageData.mesaje.length > 0) {
                        const {selectedUserId} = this.state;
                        console.log('messages:', messageData.mesaje);
                        console.log('dela:', messageData.dela);
                        console.log('selectat:', selectedUserId);

                        if (messageData.dela === selectedUserId) { // Adaugă mesajele necitite în lista locală
                            this.setState((prevState) => ({
                                messages: [...prevState.messages, {from: 'User', content: messageData.mesaje}],
                            }));

                            if (this.websocketMessage && this.websocketMessage.readyState === WebSocket.OPEN) {
                                const message = {
                                    type: 'sterge', // Tipul mesajului
                                    sender: selectedUserId,       // ID-ul adminului selectat
                                };

                                // Trimite mesajul prin WebSocket
                                this.websocketMessage.send(JSON.stringify(message));
                                console.log(`User with ID ${selectedUserId} selected, notification sent.`);
                            } else {
                                console.error('WebSocket is not open. Unable to notify admin selection.');
                            }
                        } else {
                            this.setState((prevState) => ({
                                unreadMessages: [...new Set([...prevState.unreadMessages, messageData.dela])],
                            }));
                            this.renderUsersMessagesList()
                        }

                        // Log fiecare mesaj necitit
                        messageData.mesaje.forEach((messageId, index) => {
                            console.log(`Unread message ${index + 1}: ${messageId}`);
                        });
                    }
                }
                else if (messageData.type === 'vazut') {
                    console.log('vazute');
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



    handleMessageChange = (event) => {
        this.setState({ messageInput: event.target.value });
    };

    handleMessageGroupChange = (event) => {
        this.setState({ messageInputGroup: event.target.value });
    };



    handleTyping = () => {
        const {  selectedUserId } = this.state;

        if (this.websocketMessage && this.websocketMessage.readyState === WebSocket.OPEN) {
            const typingNotification = {
                type: 'typing',
                userId: selectedUserId,
            };
            this.websocketMessage.send(JSON.stringify(typingNotification));
        }
    };

    renderChatBox = () => {
        const { messages, messageInput, typingNotification } = this.state;

        return (
            <>
                <div style={chatBoxStyle}>
                    {messages.map((msg, index) => (
                        <div key={index} >
                            {/* Iterează prin fiecare element din msg.content (care este un array) */}
                            {msg.content.map((item, i) => (
                                <div key={i} style={{
                                    backgroundColor: 'limegreen',
                                    padding: '10px',
                                    marginBottom: '10px',
                                    borderRadius: '5px',
                                    color: 'black',
                                    ...msg.from === 'You' ? adminMessageStyle : userMessageStyle  // Stilul mesajului rămâne aplicat
                                }}>
                                    {item}
                                </div>
                            ))}
                        </div>
                    ))}
                    {typingNotification && <p><em>{typingNotification}</em></p>}
                    {this.renderGreenDot()}
                </div>
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
        );
    };


    renderGroupBox = () => {
        const { messagesGroup, messageInputGroup, typingNotification } = this.state;

        return (
            <>
                <div style={chatBoxStyle}>
                    {messagesGroup.map((msg, index) => (
                        <div key={index} >
                            {/* Iterează prin fiecare element din msg.content (care este un array) */}
                            {msg.content.map((item, i) => (
                                <div key={i} style={{
                                    backgroundColor: 'limegreen',
                                    padding: '10px',
                                    marginBottom: '10px',
                                    borderRadius: '5px',
                                    color: 'black',
                                    ...msg.from === adminMessageStyle  // Stilul mesajului rămâne aplicat
                                }}>
                                    {item}
                                </div>
                            ))}
                        </div>
                    ))}
                    {this.renderGreenDot()}
                </div>
                <Input
                    type="text"
                    value={messageInputGroup}
                    onChange={this.handleMessageGroupChange}
                    onKeyUp={this.handleTyping}
                    placeholder="Type your message..."
                />
                <Button onClick={this.sendMessageGroup} style={{ marginTop: '10px' }}>
                    Send
                </Button>
            </>
        );
    };

    renderUsersMessagesList = () => {
        const { usersMessages, selectedUserId, unreadMessages } = this.state;

        // Functia de golire a mesajelor
        const clearMessages = () => {
            // Adaugă logica pentru a goli lista de mesaje aici
            this.setState({ messages: [] }); // Exemplu: presupunând că `messages` este lista de mesaje
        };

        return (
            <ListGroup>
                {usersMessages.map((user) => (
                    <ListGroupItem
                        key={user.id}
                        onClick={() => {
                            clearMessages(); // Golește lista de mesaje înainte de a selecta adminul
                            this.handleUserSelection(user.id); // Apoi selectează adminul
                        }}
                        style={{
                            cursor: 'pointer',
                            backgroundColor: user.id === selectedUserId ? '#007bff' : '#fff',
                            color: user.id === selectedUserId ? '#fff' : '#000',
                        }}
                    >
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                            <strong>{user.username}</strong>

                            {/* Blue dot if admin ID is in unreadMessages */}
                            {unreadMessages.includes(user.id) && (
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


    renderUsersGroupList = () => {
        const { usersGroup, selectedUserIds} = this.state;


        // Functia pentru selectarea/deselectarea utilizatorilor
        const toggleUserGroupSelection = (userId) => {
            this.setState((prevState) => {
                const isSelected = prevState.selectedUserIds.includes(userId);
                return {
                    selectedUserIds: isSelected
                        ? prevState.selectedUserIds.filter((id) => id !== userId) // Deselectează
                        : [...prevState.selectedUserIds, userId], // Selectează
                };
            });
        };

        return (
            <ListGroup>
                {usersGroup.map((user) => (
                    <ListGroupItem
                        key={user.id}
                        onClick={() => {
                            toggleUserGroupSelection(user.id);
                        }}
                        style={{
                            cursor: 'pointer',
                            backgroundColor: selectedUserIds.includes(user.id) ? '#007bff' : '#fff',
                            color: selectedUserIds.includes(user.id) ? '#fff' : '#000',
                        }}

                    >
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                            <strong>{user.username}</strong>
                        </div>
                    </ListGroupItem>
                ))}
            </ListGroup>
        );
    };


    handleUserSelection = (userId) => {
        this.setState((prevState) => ({
            selectedUserId: userId,
            unreadMessages: prevState.unreadMessages.filter((id) => id !==userId), // Remove adminId from unreadMessages
        }));

        // Verifică dacă websocket-ul este deschis
        if (this.websocketMessage && this.websocketMessage.readyState === WebSocket.OPEN) {
            const message = {
                type: 'userSelected', // Tipul mesajului
                userId: userId,       // ID-ul adminului selectat
            };

            // Trimite mesajul prin WebSocket
            this.websocketMessage.send(JSON.stringify(message));
            console.log(`User with ID ${userId} selected, notification sent.`);
        } else {
            console.error('WebSocket is not open. Unable to notify admin selection.');
        }
    };

    sendMessageChat = () => {
        const { messageInput, selectedUserId } = this.state;

        if (!selectedUserId) {
            alert('Please select an user!');
            return;
        }

        if (messageInput.trim() === '') {
            return;
        }

        const message = {
            type: 'message',
            userId: selectedUserId,
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

    sendMessageGroup = () => {
        const { messageInputGroup, selectedUserIds } = this.state;


        if (messageInputGroup.trim() === '') {
            return;
        }

        const message = {
            type: 'message',
            userIds: selectedUserIds,
            content: [messageInputGroup],
            from: 'You', // Marca mesajul ca fiind de la utilizator
            to: "group",
        };

        // Adăugăm mesajul trimis de utilizator în lista de mesaje
        this.setState((prevState) => ({
            messagesGroup: [...prevState.messagesGroup, message],
            messageInputGroup: '', // Golim inputul după trimitere
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
    toggleDeviceList = () => {
        const { showDevices } = this.state;
        if (!showDevices) {
            this.fetchDevices();
        }
        this.setState((prevState) => ({
            showDevices: !prevState.showDevices
        }));
    };
    fetchDevices = () => {
        this.setState({ loadingDevices: true, errorDevices: null });

        API_ADMIN.fetchDevices((response, status, error) => {
            if (status === 200) {
                console.log(response);  // Log the response data
                this.setState({ devices: response, loadingDevices: false });
            } else {
                this.setState({ errorDevices: error.message, loadingDevices: false });
            }
        });
    };
    renderDeviceList = () => {
        const { devices } = this.state;
        console.log(devices);
        return (
            <ListGroup>
                {devices.map((device) => (
                    <ListGroupItem key={device.id}>
                        <strong>{device.id}</strong> - Description: {device.description},Address: {device.address},  Maximum Hourly Energy Consumption: {device.maxenergy}
                        {device.user && ` - User: ${device.user.id}`}
                    </ListGroupItem>
                ))}
            </ListGroup>
        );
    };

    toggleDeviceUpdate = () => {
        this.setState((prevState) => ({
            showDeviceUpdateForm: !prevState.showDeviceUpdateForm,
        }));
    };

    handleDeviceUpdateChange = (e) => {
        const { name, value } = e.target;
        this.setState((prevState) => ({
            updatedDevice: {
                ...prevState.updatedDevice,
                [name]: value,
            },
        }));
    };

    handleDeviceUpdateSubmit = () => {
        const { updatedDevice } = this.state;
        console.log("Updated Device:", updatedDevice);

        if(updatedDevice.maxPower==='')
            updatedDevice.maxPower=0
        if(updatedDevice.address==='')
            updatedDevice.address="neschimbat"
        if(updatedDevice.description==='')
            updatedDevice.description="neschimbat"
        console.log("Updated Device:", updatedDevice);

        API_ADMIN.updateDevices(updatedDevice, (response, status, error) => {
            if (status === 204) {
                this.setState((prevState) => ({
                    devices: prevState.devices.filter(device => device.id !== updatedDevice.id),
                    updatedDevices: { id: '', description: '', address: '', maxPower: '' },
                    loadingDevices: false
                }));
            } else {
                this.setState({
                    errorUDevices: error ? error.message : 'An error occurred during deletion',
                    updatedDevices: { id: '', description: '', address: '', maxPower: '' },
                    loadingDevices: false
                });
            }
            this.setState({
                updatedDevice: { id: '', description: '', address: '', maxPower: '' }
            });
        });
    };

    renderDeviceUpdateForm = () => {
        const { updatedDevice } = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Update Device</h3>
                <input
                    type="UUID"
                    name="id"
                    value={updatedDevice.id}
                    placeholder="ID"
                    onChange={this.handleDeviceUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="text"
                    name="description"
                    value={updatedDevice.description}
                    placeholder="Description"
                    onChange={this.handleDeviceUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="text"
                    name="address"
                    value={updatedDevice.address}
                    placeholder="Address"
                    onChange={this.handleDeviceUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="number"
                    name="maxPower"
                    value={updatedDevice.maxPower}
                    placeholder="Maximum hourly energy consumption"
                    onChange={this.handleDeviceUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />

                <Button style={buttonStyle} onClick={this.handleDeviceUpdateSubmit}>
                    Submit
                </Button>
            </div>
        );
    };

    toggleDeviceCreate = () => {
        this.setState((prevState) => ({
            showDeviceCreateForm: !prevState.showDeviceCreateForm,
        }));
    };

    handleDeviceCreateChange = (e) => {
        const { name, value } = e.target;
        this.setState((prevState) => ({
            createDevice: {
                ...prevState.createDevice,
                [name]: value,
            },
        }));
    };

    handleDeviceCreateSubmit = () => {
        const { createDevice } = this.state;
        console.log("Create Device:", createDevice);

        API_ADMIN.createDevices(createDevice, (response, status, error) => {
            if (status === 201) {
                this.setState((prevState) => ({
                    devices: [...prevState.devices, response],
                    createsDevice: { description: '', address: '', maxPower: '' },
                    loadingDevices: false
                }));
            } else {
                this.setState({ errorDevices: error.message, loadingDevices: false });
            }
        });
        this.setState({
            createDevice: { description: '', address: '', maxPower: '' }
        });
    };

    renderDeviceCreateForm = () => {
        const { createDevice } = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Create Device</h3>
                <input
                    type="text"
                    name="description"
                    value={createDevice.description}
                    placeholder="Description"
                    onChange={this.handleDeviceCreateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="text"
                    name="address"
                    value={createDevice.address}
                    placeholder="Address"
                    onChange={this.handleDeviceCreateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="number"
                    name="maxPower"
                    value={createDevice.maxPower}
                    placeholder="Maximum hourly energy consumption"
                    onChange={this.handleDeviceCreateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />

                <Button style={buttonStyle} onClick={this.handleDeviceCreateSubmit}>
                    Submit
                </Button>
            </div>
        );
    };


    toggleCreateMapping = () => {
        this.setState((prevState) => ({
            showCreateMappingForm: !prevState.showCreateMappingForm,
        }));
    };

    handleCreateMappingChange = (e) => {
        const { name, value } = e.target;
        this.setState((prevState) => ({
            createMapping: {
                ...prevState.createMapping,
                [name]: value,
            },
        }));
    };

    handleCreateMappingSubmit = () => {
        const { createMapping } = this.state;
        console.log("Create Mapping:", createMapping);

        API_ADMIN.getUserReference(createMapping, (response, status, error) => {
            console.log("status");
            console.log(status);

            if (status === 200) {
                this.setState((prevState) => ({
                    mappings: [...prevState.mappings, response],
                    createMapping: { id_device: '', id_user: '' },
                    loadingDevices: false
                }));
                console.log("aici");
                console.log(response);
                API_ADMIN.createMapping(response, createMapping.id_device, (secondResponse, secondStatus, secondError) => {
                    if (secondStatus === 200) {
                        this.setState((prevState) => ({
                            additionalData: [...prevState.additionalData, secondResponse],
                            loadingAdditionalData: false
                        }));
                    } else {
                        this.setState({
                            errorMapping: secondError.message,
                            loadingAdditionalData: false
                        });
                    }
                });
            } else {
                this.setState({
                    errorMapping: error.message,
                    loadingMappings: false
                });
            }
        });
        this.setState({
            createMapping: { id_device: '', id_user: '' }
        });
    };



    renderCreateMappingForm = () => {
        const { createMapping} = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Create Mapping</h3>
                <input
                    type="UUID"
                    name="id_user"
                    value={createMapping.id_user}
                    placeholder="id_user"
                    onChange={this.handleCreateMappingChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="UUID"
                    name="id_device"
                    value={createMapping.id_device}
                    placeholder="id_device"
                    onChange={this.handleCreateMappingChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />


                <Button style={buttonStyle} onClick={this.handleCreateMappingSubmit}>
                    Submit
                </Button>
            </div>
        );
    };
    toggleDeleteMapping = () => {
        this.setState((prevState) => ({
            showDeleteMappingForm: !prevState.showDeleteMappingForm,
        }));
    };

    handleDeleteMappingChange = (e) => {
        const { name, value } = e.target;
        this.setState((prevState) => ({
            deleteMapping: {
                ...prevState.deleteMapping,
                [name]: value,
            },
        }));
    };

    handleDeleteMappingSubmit = () => {
        const { deleteMapping } = this.state;
        console.log("Delete Mapping:", deleteMapping);

        API_ADMIN.deleteMapping(deleteMapping, (response, status, error) => {
            if (status === 201) {
                this.setState((prevState) => ({
                    mapping: [...prevState.mappings, response],
                    deleteMapping: { id_device: ''},
                    loadingDevices: false
                }));
            } else {
                this.setState({ errorMapping: error.message, loadingMappings: false });
            }
        });
        this.setState({
            deleteMapping: { id_device: ''}
        });
    };

    renderDeleteMappingForm = () => {
        const { deleteMapping} = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Delete Mapping</h3>
                <input
                    type="UUID"
                    name="id_device"
                    value={deleteMapping.id_device}
                    placeholder="id_device"
                    onChange={this.handleDeleteMappingChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />



                <Button style={buttonStyle} onClick={this.handleDeleteMappingSubmit}>
                    Submit
                </Button>
            </div>
        );
    };


    toggleDeviceDelete = () => {
        this.setState((prevState) => ({
            showDeviceDeleteForm: !prevState.showDeviceDeleteForm,
        }));
    };

    handleDeviceDeleteChange = (e) => {
        const { name, value } = e.target;
        this.setState((prevState) => ({
            deleteDevice: {
                ...prevState.deleteDevice,
                [name]: value,
            },
        }));
    };

    handleDeviceDeleteSubmit = () => {
        const { deleteDevice } = this.state;
        console.log("Delete Device:", deleteDevice);

        API_ADMIN.deleteDevices(deleteDevice, (response, status, error) => {
            if (status === 204) {
                this.setState((prevState) => ({
                    devices: prevState.devices.filter(device => device.id !== deleteDevice.id),
                    loadingDevices: false
                }));
            } else {
                this.setState({
                    errorDevices: error ? error.message : 'An error occurred during deletion',
                    loadingDevices: false
                });
            }
        });
        this.setState({
            deleteDevice: { id: '' }
        });
    };


    renderDeviceDeleteForm = () => {
        const { deleteDevice } = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Delete Device</h3>
                <input
                    type="UUID"
                    name="id"
                    value={deleteDevice.id}
                    placeholder="ID"
                    onChange={this.handleDeviceDeleteChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />

                <Button style={buttonStyle} onClick={this.handleDeviceDeleteSubmit}>
                    Submit
                </Button>
            </div>
        );
    };

    toggleUserList = () => {
        const { showUsers } = this.state;
        if (!showUsers) {
            this.fetchUsers();
        }
        this.setState((prevState) => ({
            showUsers: !prevState.showUsers
        }));
    };

    fetchUsers = () => {
        this.setState({ loadingUsers: true, errorUsers: null });

        API_ADMIN.fetchUsers((response, status, error) => {
            if (status === 200) {
                this.setState({ users: response, loadingUsers: false });
            } else {
                this.setState({ errorUsers: error.message, loadingUsers: false });
            }
        });
    };

    renderUserList = () => {
        const { users } = this.state;
        return (
            <ListGroup>
                {users.map((user) => (
                    <ListGroupItem key={user.id}>
                        <strong>{user.id}</strong> - Username:{user.username},Name: {user.name}, Admin: {user.isAdmin ? 'Yes' : 'No'}
                    </ListGroupItem>
                ))}
            </ListGroup>
        );
    };

    toggleUserUpdate = () => {
        this.setState((prevState) => ({
            showUserUpdateForm: !prevState.showUserUpdateForm,
        }));
    };

    handleUserUpdateChange = (e) => {
        const { name, value } = e.target;
        this.setState((prevState) => ({
            updatedUser: {
                ...prevState.updatedUser,
                [name]: value,
            },
        }));
    };

    handleUserUpdateSubmit = () => {
        const { updatedUser } = this.state;
        console.log("Updated User:", updatedUser);
        if(updatedUser.password==='')
            updatedUser.password="neschimbat"
        if(updatedUser.name==='')
            updatedUser.name="neschimbat"
        console.log("Updated User:", updatedUser);

        API_ADMIN.updateUsers(updatedUser, (response, status, error) => {
            if (status === 204) {
                this.setState((prevState) => ({
                    users: prevState.users.filter(user => user.username !== updatedUser.username),
                    updatedUser: { id: '', username: '', name: '', password: '', isAdmin: false },
                    loadingUsers: false
                }));
            } else {
                this.setState({
                    errorUsers: error ? error.message : 'An error occurred during deletion',
                    loadingUsers: false
                });
            }
            this.setState({
                updatedUser: { id: '', username: '', name: '', password: '', isAdmin: false }
            });
        });

    };

    renderUserUpdateForm = () => {
        const { updatedUser } = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Update User</h3>
                <input
                    type="UUID"
                    name="id"
                    value={updatedUser.id}
                    placeholder="ID"
                    onChange={this.handleUserUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="text"
                    name="username"
                    value={updatedUser.username}
                    placeholder="Username"
                    onChange={this.handleUserUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="text"
                    name="name"
                    value={updatedUser.name}
                    placeholder="Name"
                    onChange={this.handleUserUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="password"
                    name="password"
                    value={updatedUser.password}
                    placeholder="Password"
                    onChange={this.handleUserUpdateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <label>
                    <input
                        type="checkbox"
                        name="isAdmin"
                        checked={updatedUser.isAdmin}
                        onChange={(e) => this.setState((prevState) => ({
                            updatedUser: { ...prevState.updatedUser, isAdmin: e.target.checked }
                        }))}
                    />
                    Is Admin
                </label>
                <Button style={buttonStyle} onClick={this.handleUserUpdateSubmit}>
                    Submit
                </Button>
            </div>
        );
    };

    toggleUserCreate = () => {
        this.setState((prevState) => ({
            showUserCreateForm: !prevState.showUserCreateForm,
        }));
    };
    handleUserCreateChange = (e) => {
        const { name, value, type, checked } = e.target; // Destructure the event object
        this.setState((prevState) => ({
            createUser: {
                ...prevState.createUser,
                [name]: type === 'checkbox' ? checked : value, // Check if it's a checkbox
            },
        }));
    };



    handleUserCreateSubmit = () => {
        const { createUser } = this.state;
        console.log("Create User:", createUser);
        if (createUser.isAdmin === undefined) {
            createUser.isAdmin = false;
        }

        API_ADMIN.createUsers(createUser, (response, status, error) => {
            if (status === 201) {
                this.setState((prevState) => ({
                    users: [...prevState.users, response],
                    loadingUsers: false
                }));
            } else {
                this.setState({ errorUsers: error.message, loadingUsers: false });
            }
            this.setState({
                createUser: { id: '', username: '', name: '', password: '', isAdmin: false }
            });
        });
    };


    renderUserCreateForm = () => {
        const { createUser } = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Create User</h3>
                <input
                    type="text"
                    name="username"
                    value={createUser.username}
                    placeholder="Username"
                    onChange={this.handleUserCreateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="text"
                    name="name"
                    value={createUser.name}
                    placeholder="Name"
                    onChange={this.handleUserCreateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <input
                    type="password"
                    name="password"
                    value={createUser.password}
                    placeholder="Password"
                    onChange={this.handleUserCreateChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <label>
                    <input
                        type="checkbox"
                        name="isAdmin"
                        checked={createUser.isAdmin}
                        onChange={this.handleUserCreateChange}
                    />
                    Is Admin
                </label>

                <Button style={buttonStyle} onClick={this.handleUserCreateSubmit}>
                    Submit
                </Button>
            </div>
        );
    };

    toggleUserDelete = () => {
        this.setState((prevState) => ({
            showUserDeleteForm: !prevState.showUserDeleteForm,
        }));
    };

    handleUserDeleteChange = (e) => {
        const { name, value } = e.target;
        this.setState((prevState) => ({
            deleteUser: {
                ...prevState.deleteUser,
                [name]: value,
            },
        }));
    };

    handleUserDeleteSubmit = () => {
        const { deleteUser } = this.state;
        console.log("Delete User:", deleteUser);

        API_ADMIN.deleteUsers(deleteUser, (response, status, error) => {
            if (status === 204) {
                this.setState((prevState) => ({
                    users: prevState.users.filter(user => user.username !== deleteUser.username),
                    loadingUsers: false
                }));
            } else {
                this.setState({
                    errorUsers: error ? error.message : 'An error occurred during deletion',
                    loadingUsers: false
                });
            }
            this.setState({
                deleteUser: { id: '' }
            });
        });
    };

    renderUserDeleteForm = () => {
        const { deleteUser } = this.state;

        return (
            <div style={formContainerStyle}>
                <h3>Delete User</h3>
                <input
                    type="text"
                    name="username"
                    value={deleteUser.username}
                    placeholder="Username"
                    onChange={this.handleUserDeleteChange}
                    style={{ width: '100%', padding: '8px', margin: '10px 0' }}
                />
                <Button style={buttonStyle} onClick={this.handleUserDeleteSubmit}>
                    Submit
                </Button>
            </div>
        );
    };

    render() {
        const {
            showUsers, showDevices, loadingUsers, loadingDevices,
            errorUsers, errorDevices, showDeviceUpdateForm, showDeviceCreateForm, showDeviceDeleteForm,
            showUserUpdateForm, showUserCreateForm, showUserDeleteForm, showCreateMappingForm, showDeleteMappingForm,
            showChatSection , showGroupSection// Noua stare pentru vizibilitatea secțiunii cu utilizatori și chat
        } = this.state;

        return (
            <div style={backgroundStyle}>
                <div style={overlayStyle}></div> {/* Dark overlay for contrast */}
                <Container style={containerStyle}>

                    {/* Left section: Buttons */}
                    <div style={leftSectionStyle}>
                        <h2>Admin Panel</h2>
                        <Button style={buttonStyle} onClick={this.toggleDeviceList} >
                            {showDevices ? 'Hide Devices' : 'Show Devices'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleDeviceUpdate} >
                            {showDeviceUpdateForm ? 'Hide Update Device' : 'Update Device'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleDeviceCreate} >
                            {showDeviceCreateForm ? 'Hide Create Device' : 'Create Device'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleDeviceDelete} >
                            {showDeviceDeleteForm ? 'Hide Delete Device' : 'Delete Device'}
                        </Button>

                        <Button style={buttonStyle} onClick={this.toggleUserList} >
                            {showUsers ? 'Hide Users' : 'Show Users'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleUserUpdate} >
                            {showUserUpdateForm ? 'Hide Update User' : 'Update User'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleUserCreate} >
                            {showUserCreateForm ? 'Hide Create User' : 'Create User'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleUserDelete} >
                            {showUserDeleteForm ? 'Hide Delete User' : 'Delete User'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleCreateMapping} >
                            {showCreateMappingForm ? 'Hide Create Mapping user-device' : 'Create Mapping user-device'}
                        </Button>
                        <Button style={buttonStyle} onClick={this.toggleDeleteMapping} >
                            {showDeleteMappingForm ? 'Hide Delete Mapping user-device' : 'Delete Mapping user-device'}
                        </Button>

                        {/* Buton pentru a activa/dezactiva secțiunea Users și Chat */}
                        <Button style={buttonStyle} onClick={() => this.setState({ showChatSection: !showChatSection })}>
                            {showChatSection ? 'Hide Chat Section' : 'Show Chat Section'}
                        </Button>

                        <Button style={buttonStyle} onClick={() => this.setState({ showGroupSection: !showGroupSection })}>
                            {showGroupSection ? 'Hide Group Section' : 'Show Group Section'}
                        </Button>
                    </div>

                    {/* Right section: Conditionally render forms */}
                    {showDeviceUpdateForm && this.renderDeviceUpdateForm()}
                    {showDeviceCreateForm && this.renderDeviceCreateForm()}
                    {showDeviceDeleteForm && this.renderDeviceDeleteForm()}
                    {loadingDevices && <p>Loading Devices...</p>}
                    {errorDevices && <p style={{ color: 'red' }}>Error: {errorDevices}</p>}
                    {showDevices && !loadingDevices && !errorDevices && this.renderDeviceList()}

                    {showUserUpdateForm && this.renderUserUpdateForm()}
                    {showUserCreateForm && this.renderUserCreateForm()}
                    {showUserDeleteForm && this.renderUserDeleteForm()}
                    {showCreateMappingForm && this.renderCreateMappingForm()}
                    {showDeleteMappingForm && this.renderDeleteMappingForm()}
                    {loadingUsers && <p>Loading Users...</p>}
                    {errorUsers && <p style={{ color: 'red' }}>Error: {errorUsers}</p>}
                    {showUsers && !loadingUsers && !errorUsers && this.renderUserList()}

                    {/* Secțiunea Users și Chat afișată condiționat */}
                    {showChatSection && (
                        <div style={bottomRightStyle}>
                            <h4>Users</h4>
                            {this.renderUsersMessagesList()}

                            <h5>Chat</h5>
                            {this.renderChatBox()}
                        </div>
                    )}

                    {showGroupSection && (
                        <div style={bottomRightStyle}>
                            <h4>Users</h4>
                            {this.renderUsersGroupList()}

                            <h5>Group</h5>
                            {this.renderGroupBox()}
                        </div>
                    )}

                </Container>
            </div>
        );
    }

}

export default withRouter(Admin);
