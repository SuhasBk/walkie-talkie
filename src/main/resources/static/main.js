let audioStream;
let mediaRecorder;
let audioChunks = [];
let recordedArrayBuffer = null;
let audioContext;

let ws = new WebSocket("/communicate");
ws.binaryType = 'arraybuffer';

ws.onmessage = msg => {
    const decoder = new TextDecoder('utf-8');
    let data = decoder.decode(msg.data);
    if (data !== 'OK') {
        playAudio(msg.data);
    } else {
        console.log(data);
    }
};

ws.onopen = () => {
    console.log("WebSocket connection established.");

};

ws.onclose = () => {
    console.log("WebSocket connection closed.");
};

// Function to create or resume AudioContext
function initAudioContext() {
    if (!audioContext) {
        audioContext = new (window.AudioContext || window.webkitAudioContext)();
    }
    // Resume the context if it's suspended
    if (audioContext.state === 'suspended') {
        return audioContext.resume().then(() => {
            console.log('AudioContext resumed');
        });
    }
    return Promise.resolve();
}

document.addEventListener('DOMContentLoaded', () => {
    initAudioContext();
});

// Start recording
function startRecording() {
    initAudioContext().then(() => {
        navigator.mediaDevices.getUserMedia({ audio: true })
            .then(stream => {
                audioStream = stream;
                mediaRecorder = new MediaRecorder(stream);

                // Collect audio data chunks
                mediaRecorder.ondataavailable = event => {
                    audioChunks.push(event.data);
                };

                // When recording stops, process the audio and store as ArrayBuffer
                mediaRecorder.onstop = () => {
                    const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
                    audioChunks = [];

                    // Convert Blob to ArrayBuffer
                    const reader = new FileReader();
                    reader.onloadend = function () {
                        recordedArrayBuffer = reader.result;
                        console.log("Recording stopped and ArrayBuffer is ready.", recordedArrayBuffer);
                        // playAudio(recordedArrayBuffer);
                        sendAudio(recordedArrayBuffer);
                    };
                    reader.readAsArrayBuffer(audioBlob);
                };

                mediaRecorder.start();
                console.log("Recording started...");
            })
            .catch(err => {
                console.error("Error accessing microphone: ", err);
            });
    });
}

// Stop recording
function stopRecording() {
    if (mediaRecorder) {
        mediaRecorder.stop();
        audioStream.getTracks().forEach(track => track.stop());
        console.log("Recording stopped.");
    }
}

// Play the recorded audio from ArrayBuffer
function playAudio(arrayBuffer) {
    if (!audioContext) return; // Ensure AudioContext is initialized

    console.log(arrayBuffer);

    // Decode the ArrayBuffer into an AudioBuffer
    audioContext.decodeAudioData(arrayBuffer)
        .then(audioBuffer => {
            const source = audioContext.createBufferSource();
            source.buffer = audioBuffer;
            source.connect(audioContext.destination);
            source.start();
            console.log("Playing recorded audio...");
        })
        .catch(error => {
            console.error("Error decoding audio data: ", error);
        });
}

function sendAudio(data) {
    ws.send(data);
}