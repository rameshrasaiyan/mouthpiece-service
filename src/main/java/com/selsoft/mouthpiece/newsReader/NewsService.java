package com.selsoft.mouthpiece.newsReader;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.NotFound;
import com.jaunt.UserAgent;
import com.jaunt.util.Filter;

public class NewsService {
	
	private String url;
	
	public String getUrl() {
		return url;
	}
	
	public NewsService(){}
	
	public NewsService(String url) {
		this.url = url;
	}
	
	public String getNewsContent() throws JauntException {
		String newsContent = StringUtils.EMPTY;
		NewsReader newsReader = null;
		if(StringUtils.contains(url, "apple.news")) {
			AppleNewsService appleNewsReader = new AppleNewsService(url);
			newsContent = appleNewsReader.extractNewsContent();
		} else {
			newsReader = getNewsReader(url);
			if(newsReader != null) {
				newsContent = newsReader.toString();
			}
		}
		
		return newsContent;
	}
	
	public NewsReader getNewsReader(String url) throws JauntException {
		NewsReader newsReader = null;
		if(StringUtils.contains(url, "www.vox.com")) {
			newsReader = new VoxNewsReader(url);
		} else if(StringUtils.contains(url, "www.foxnews.com")) {
			newsReader = new FoxNewsReader(url);
		} else if(StringUtils.contains(url, "www.washingtonpost.com")) {
			newsReader = new WashingtonPostNewsReader(url);
		} else if(StringUtils.contains(url, "time.com")) {
			newsReader = new TimeNewsReader(url);
		} else if(StringUtils.contains(url, "www.cnn.com")) {
			newsReader = new CnnNewsReader(url);
		} else if(StringUtils.contains(url,  "www.hindustantimes.com")) {
			newsReader = new HindustanTimesNewsReader(url);
		} else if(StringUtils.contains(url, "")) {
			newsReader = new HinduNewsReader(url);
		}
		return newsReader;
	}
	
}

class AppleNewsService extends NewsService {
	
	public AppleNewsService() {}

	public AppleNewsService(String url) {
		super(url);
	}
	
	public String extractNewsContent() throws JauntException {
		NewsReader newsReader = super.getNewsReader(getActualNewsWebsite());
		if(newsReader != null) {
			return newsReader.toString(); 
		}
		return StringUtils.EMPTY;
	}
	
	private String getActualNewsWebsite() throws JauntException {
		String actualWebUrl = null;
		String appleWebUrl = super.getUrl();
		if(StringUtils.isNotBlank(appleWebUrl)) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(appleWebUrl);							//visit another url.
			String webContent = userAgent.doc.innerHTML().toString();
			int startIndex = StringUtils.indexOf(webContent, "redirectToUrl(") + 15;
			webContent = StringUtils.substring(webContent, startIndex);
			int endIndex = StringUtils.indexOf(webContent, "\"");
			actualWebUrl = StringUtils.substring(webContent, 0, endIndex);
		}
		return actualWebUrl;
	}
}

abstract class NewsReader {
	
	private String newsUrl = null;
	private String pageTitle = null;
	private String pageSubTitle = null;
	private String newsBody = null;
	
	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = StringUtils.appendIfMissing(StringUtils.trimToEmpty(pageTitle), ". ");
	}
	
	public String getPageSubTitle() {
		return pageSubTitle;
	}

	public void setPageSubTitle(String pageSubTitle) {
		this.pageSubTitle = StringUtils.appendIfMissing(StringUtils.trimToEmpty(pageSubTitle), ". ");
	}
	
	public String getNewsBody() {
		return newsBody;
	}
	
	public void setNewsBody(String newsBody) {
		this.newsBody = newsBody;
	}
	
	public abstract void getNewsContent() throws JauntException;
	
	public String toString() {
		StringBuffer newsContentBuffer = new StringBuffer();
		newsContentBuffer.append(StringUtils.isNotBlank(pageTitle) ? pageTitle : StringUtils.EMPTY + StringUtils.CR);
		newsContentBuffer.append(StringUtils.isNotBlank(pageSubTitle) ? pageSubTitle : StringUtils.EMPTY + StringUtils.CR);
		newsContentBuffer.append(newsBody);
		String newsInString = newsContentBuffer.toString().replace("’", "&apos;").replaceAll("“", "&quot;").replaceAll("”", "&quot;").replaceAll("—", "-")
								.replaceAll("<", "&lt;").replaceAll(">", "&rt;").replaceAll("\n", "");
		byte[] newsInBytes = newsInString.getBytes(StandardCharsets.ISO_8859_1);
		newsInString = new String(newsInBytes, StandardCharsets.UTF_8);
		return newsInString;
	}
}

class VoxNewsReader extends NewsReader {
	
	public VoxNewsReader(String url) throws JauntException {
		super.setNewsUrl(url);
		getNewsContent();
	}
	
	public void getNewsContent() throws JauntException {
		if(StringUtils.isNotBlank(super.getNewsUrl())) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(super.getNewsUrl());
			userAgent.setFilter(new Filter() { //subclass Filter to create custom filter
				public boolean childElementAllowed(Element parent, Element child){ //override callback method
					if(child.getName().equals("figure")){ //only allow tags named 'message'
						parent.removeChildren();
						return true;
					} else {
						return false;
					}
				}
			});
			Element titleElement = userAgent.doc.findFirst("<h1 class=\"c-page-title\">");
			Element subTitleElement = userAgent.doc.findFirst("<h2 class=\"c-entry-summary p-dek\">");
			super.setPageTitle(titleElement.innerText());
			super.setPageSubTitle(subTitleElement.innerText());
			Element entryContent = userAgent.doc.findFirst("<div class=\"c-entry-content\">");
			
			super.setNewsBody(entryContent.innerText().replace("\n", ""));
		}
	}
}

class FoxNewsReader extends NewsReader {
	
	public FoxNewsReader(String url) throws JauntException {
		super.setNewsUrl(url);
		getNewsContent();
	}
	
	public void getNewsContent() throws JauntException {
		if(StringUtils.isNotBlank(super.getNewsUrl())) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(super.getNewsUrl());
			userAgent.setFilter(new Filter() { //subclass Filter to create custom filter
				public boolean childElementAllowed(Element parent, Element child){ //override callback method
					if(child.getName().equals("figure")){ //only allow tags named 'message'
						parent.removeChildren();
						return true;
					} else {
						return false;
					}
				}
			});
			Element titleElement = userAgent.doc.findFirst("<header class=\"article-header\">").findFirst("<h1>");
			super.setPageTitle(titleElement.innerText());
			Element entryContent = userAgent.doc.findFirst("<div class=\"article-content\">");
			/*Elements entryContentElements = entryContent.findEvery("<p>");
			StringBuffer newsBodyBuffer = new StringBuffer();
			
			for(Element paraElement : entryContentElements) {
				newsBodyBuffer.append(paraElement.innerText() + StringUtils.CR);
			}
			newsBody = newsBodyBuffer.toString();*/
			super.setNewsBody(entryContent.innerText().replace("\n", ""));
		}
	}
}

class WashingtonPostNewsReader extends NewsReader {
	
	public WashingtonPostNewsReader(String url) throws JauntException {
		super.setNewsUrl(url);
		getNewsContent();
	}
	
	public void getNewsContent() throws JauntException {
		if(StringUtils.isNotBlank(super.getNewsUrl())) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(super.getNewsUrl());
			Elements unwantedElements = userAgent.doc.findEvery("<div class=\"inline-content\">");
			for(Element unwantedElement : unwantedElements) {
				unwantedElement.innerHTML("");
			}
			
			Element titleElement = userAgent.doc.findFirst("<h1 itemprop=\"headline\" data-pb-field=\"customFields.web_headline\">");
			super.setPageTitle(titleElement.innerText());
			Element entryContent = userAgent.doc.findFirst("<article itemprop=\"articleBody\">");
			
			/*Elements entryContentElements = entryContent.findEvery("<p>");
			StringBuffer newsBodyBuffer = new StringBuffer();
			
			for(Element paraElement : entryContentElements) {
				newsBodyBuffer.append(paraElement.innerText() + StringUtils.CR);
			}
			newsBody = newsBodyBuffer.toString();*/
			super.setNewsBody(entryContent.innerText().replace("\n", ""));
		}
	}
}

class TimeNewsReader extends NewsReader {
	
	public TimeNewsReader(String url) throws JauntException {
		super.setNewsUrl(url);
		getNewsContent();
	}
	
	public void getNewsContent() throws JauntException {
		if(StringUtils.isNotBlank(super.getNewsUrl())) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(super.getNewsUrl());
			Element titleElement = userAgent.doc.findFirst("<h1 class=\"_8UFs4BVE\">");
			super.setPageTitle(titleElement.innerText());
			Element entryContent = userAgent.doc.findFirst("<article class=\"row\">");
			super.setNewsBody(entryContent.innerText().replace("\n", ""));
		}
	}
}

class CnnNewsReader extends NewsReader {
	
	public CnnNewsReader(String url) throws JauntException {
		super.setNewsUrl(url);
		getNewsContent();
	}
	
	public void getNewsContent() throws JauntException {
		if(StringUtils.isNotBlank(super.getNewsUrl())) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(super.getNewsUrl());
			Element titleElement = userAgent.doc.findFirst("<h1 class=\"pg-headline\">");
			super.setPageTitle(titleElement.innerText());
			Element entryContent = userAgent.doc.findFirst("<div itemprop=\"articleBody\">");
			super.setNewsBody(entryContent.innerText().replace("\n", " "));
			//super.setNewsBody(entryContent.innerText());
		}
	}
}

class HindustanTimesNewsReader extends NewsReader {
	
	public HindustanTimesNewsReader(String url) throws JauntException {
		super.setNewsUrl(url);
		getNewsContent();
	}
	
	public void getNewsContent() throws JauntException {
		if(StringUtils.isNoneBlank(super.getNewsUrl())) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(super.getNewsUrl());
			Element topContainer = userAgent.doc.findFirst("<section class=\"container\">");
			Element titleElement = topContainer.findFirst("<h1 itemprop=\"headline\">");
			Element subTitleElement = topContainer.findFirst("<h2>");
			super.setPageTitle(titleElement.innerText());
			super.setPageSubTitle(subTitleElement.innerText());
			Element entryContent = userAgent.doc.findFirst("<div class=\"story-details\" itemprop=\"articleBody\">");
			super.setNewsBody(entryContent.innerText().replace("\n", " "));
		}
	}
}

class HinduNewsReader extends NewsReader {
	
	public HinduNewsReader(String url) throws JauntException {
		super.setNewsUrl(url);
		getNewsContent();
	}
	
	public void getNewsContent() throws JauntException {
		if(StringUtils.isNoneBlank(super.getNewsUrl())) {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(super.getNewsUrl());
			Element topContainer = userAgent.doc.findFirst("<div class=\"article\">");
			Element titleElement = topContainer.findFirst("<h1 class=\"title\">");
			Element subTitleElement = null;
			try {
				subTitleElement = topContainer.findFirst("<h2 class=\"intro\">");
				super.setPageSubTitle(subTitleElement.innerText());
			} catch(NotFound e) {
				System.out.println("sub title not found");
			}
			super.setPageTitle(titleElement.innerText());
			
			Elements entryContents = topContainer.findEvery("<div id=");
			String idAttribute = null;
			for(Element entryContent : entryContents) {
				try {
					idAttribute = entryContent.getAt("id");
					if(StringUtils.contains(idAttribute, "content-body-")) {
						super.setNewsBody(entryContent.innerText().replace("\n", " "));
						break;
					}
				} catch(NotFound n) {}
				catch(Exception e) {}
				
			}
		}
	}
}