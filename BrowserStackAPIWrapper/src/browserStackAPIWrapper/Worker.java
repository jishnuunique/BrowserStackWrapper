package browserStackAPIWrapper;

import org.json.simple.JSONObject;

// not been integrated yet with BrowserStackWrapper

public class Worker {

	private String id;
	private String url;

	public Worker(JSONObject data) {
		this.id = (String) data.get("id");
		this.url = (String) data.get("url");
	}

	public Worker get_worker() {
		return this;
	}
}
