package com.selsoft.mouthpiece.controller;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.selsoft.mouthpiece.newsReader.NewsService;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

@RestController("/mouthpiece")
public class MouthPieceController {
	
	private static final Logger logger = LogManager.getLogger(MouthPieceController.class);
	
	private final String userName = "9924ff83-6a13-440e-b2eb-0af16cefd855";
	private final String password = "q0v1F1BXe3r1";
	private final String noTextFoundFile = "resources/NoTextFoundToConvert.wav";
	
	private NewsService newsReader = null;
	
	@RequestMapping(value="/convertTextToMp3", method=RequestMethod.GET, produces="audio/mp3")
	@ResponseBody
	public void convertTextToMp3(@RequestParam(value = "text", defaultValue = "") String url,
			@RequestParam(value = "voice", defaultValue = "en-US_AllisonVoice") String voice,
			@RequestParam(value = "isDownload", defaultValue = "") String isDownload, 
			HttpServletResponse response) {
		newsReader = new NewsService(url);
		String newsContent = null;
		try {
			newsContent = newsReader.getNewsContent();
			generateAudioFile(newsContent, voice, isDownload, response);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@RequestMapping(value="/generateAudioFile", method=RequestMethod.GET, produces="audio/mp3")
	@ResponseBody
	public void generateAudioFile(@RequestParam(value = "text", defaultValue = "") String text,
			@RequestParam(value = "voice", defaultValue = "en-US_AllisonVoice") String voice,
			@RequestParam(value = "isDownload", defaultValue = "") String isDownload, 
			HttpServletResponse response) {
		/*InputStream in = null;
		OutputStream out = null;
		InputStream targetInputStream = null;*/
		
		InputStream targetInputStream = null;
		int bytesInText = 0; 
		//File file = new File("c:\\users\\mail2\\desktop\\test.mp3");
		OutputStream out = null;
		
		try {
			if (StringUtils.isBlank(text)) {
				targetInputStream = this.getClass().getClassLoader().getResourceAsStream(noTextFoundFile);
			} else {
				//targetInputStream = convertTextToMp3(text, voice);
				out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=synthesizedText.mp3");
				String tempString = null;
				bytesInText = text.getBytes().length;
				int numberOfArrays = (bytesInText)/1000 + ((bytesInText % 1000 > 0)? 1 : 0);
				String[] newsInArray = new String[numberOfArrays];
				int stringIndex = 0, lastWhiteSpaceIndex = 0;
				while(text.length() > 1000) {
					tempString = StringUtils.substring(text, 0, 1000);
					lastWhiteSpaceIndex = StringUtils.lastIndexOf(tempString, StringUtils.SPACE);
					tempString = StringUtils.substring(text, 0, lastWhiteSpaceIndex);
					text = text.substring(lastWhiteSpaceIndex);
					newsInArray[stringIndex++] = tempString;
				}
				if(StringUtils.isNotBlank(text)) {
					newsInArray[stringIndex] = text;
				}
				byte[] buffer;
				int read;
				for(String textValue : newsInArray) {
					
					targetInputStream = convertTextToMp3(textValue, "en-US_AllisonVoice");
					buffer = new byte[500];
					while ((read = targetInputStream.read(buffer)) != -1) {
						out.write(buffer, 0, read);
					}
					
				}
			}
			/*out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=synthesizedText.mp3");
			byte[] buffer = new byte[2048];
			int read;
			while ((read = targetInputStream.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}*/
			
		} catch (Exception e) {
			// Log something and return an error message
			logger.error("got error: " + e.getMessage(), e);
			// resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
			// e.getMessage());
		} finally {
			close(out);
			close(targetInputStream);
		}
		
		//return Response.ok(targetInputStream, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment; filename=" + target.getName() ).build();
	}
	
	public InputStream convertTextToMp3(String text, String voice) {
		FileOutputStream wavOutputStream = null;
		InputStream in = null;
		InputStream targetInputStream = null;
		try {
			TextToSpeech textService = new TextToSpeech();
			textService.setUsernameAndPassword(userName, password);
			in = textService.synthesize(text, new Voice(voice, null, null), "audio/wav");
			File sourceFile = File.createTempFile("source", ".wav");
			
			sourceFile.deleteOnExit();
			try{
				wavOutputStream = new FileOutputStream(sourceFile);
				IOUtils.copy(in, wavOutputStream);
			} catch(Exception e) {
				logger.error("got error: " + e.getMessage(), e);
			}
			
			targetInputStream = new FileInputStream(convertWavToMp3(sourceFile));
		} catch(Exception e) {
			logger.error("Error while converting text to mp3", e);
		} finally {
			//close(in);
			//close(targetInputStream);
			//close(wavOutputStream);
		}
		return targetInputStream;
	}

	private File convertWavToMp3(File sourceFile) throws InputFormatException, EncoderException {
		File target = new File("targetFile.mp3");
		AudioAttributes audioAttributes = new AudioAttributes();
		audioAttributes.setCodec("libmp3lame");
		audioAttributes.setBitRate(new Integer(16000));
		audioAttributes.setChannels(new Integer(2));
		audioAttributes.setSamplingRate(new Integer(11025));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audioAttributes);
		Encoder encoder = new Encoder();
		encoder.encode(sourceFile, target, attrs);
		return target;
	}
	
	private void close(Closeable closeable) {
	    if (closeable != null) {
	        try {
	            closeable.close();
	        } catch (IOException e) {
	            // ignore
	        }
	    }	      	   
	}

}
