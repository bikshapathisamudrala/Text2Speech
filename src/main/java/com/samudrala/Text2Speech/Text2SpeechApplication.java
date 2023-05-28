package com.samudrala.Text2Speech;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Text2SpeechApplication {

	private final Voice agentVoice;
	private final Voice customerVoice;

	public Text2SpeechApplication() {
		// Initialize agent's voice
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		agentVoice = VoiceManager.getInstance().getVoice("kevin16");
		if (agentVoice != null) {
			agentVoice.allocate();
		}

		// Initialize customer's voice
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		customerVoice = VoiceManager.getInstance().getVoice("kevin");
		if (customerVoice != null) {
			customerVoice.allocate();
		}
	}

	// @PostMapping(value = "/conversation", consumes =
	// MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<byte[]> convertConversationToSpeech(@RequestBody Conversation conversation) {
		try {

			// Extract agent and customer messages from the conversation object
			for (Message message : conversation.getMessages()) {
				if (message.getSender().equals("agent")) {
					agentVoice.speak(message.getText());
				} else if (message.getSender().equals("customer")) {
					customerVoice.speak(message.getText());
				}
			}

			// Convert agent's message to speech using agent's voice

			// Convert customer's message to speech using customer's voice

			// Return the audio bytes as a response
			// You can modify the response format as per your requirements
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(combineAudioBytes("".getBytes(), "".getBytes()));
		} catch (Exception e) {
			// Handle any exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private byte[] combineAudioBytes(byte[] audioBytes1, byte[] audioBytes2) {
		// Combine audio bytes from agent's voice and customer's voice
		// You can implement your own logic to combine or manipulate the audio bytes
		// This example simply concatenates them
		byte[] combinedBytes = new byte[audioBytes1.length + audioBytes2.length];
		System.arraycopy(audioBytes1, 0, combinedBytes, 0, audioBytes1.length);
		System.arraycopy(audioBytes2, 0, combinedBytes, audioBytes1.length, audioBytes2.length);
		return combinedBytes;
	}

	public static void main(String[] args) {
		SpringApplication.run(Text2SpeechApplication.class, args);

		// Create messages for the conversation
		List<Message> messages = new ArrayList();
		messages.add(new Message("agent", "Hello kevin, how can I assist you?"));
		messages.add(new Message("customer", "I have a question about your product."));
		messages.add(new Message("agent", "Sure, what would you like to know?"));
		messages.add(new Message("customer", "I see there are somany verites of ."));
		listAllVoices();

		// Create a Conversation object with the messages
		Text2SpeechApplication text2SpeechApplication = new Text2SpeechApplication();
		Conversation conversation = new Conversation(messages);
		text2SpeechApplication.convertConversationToSpeech(conversation);

	}
	
	public static void listAllVoices() {
		System.out.println();
		System.out.println("All voices available:");
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice[] voices = voiceManager.getVoices();
		for (int i = 0; i < voices.length; i++) {
			System.out.println("    " + voices[i].getName()
					+ " (" + voices[i].getDomain() + " domain)");
		}
	}
}
