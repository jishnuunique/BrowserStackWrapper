package browserStackAPIWrapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;

/**
 *
 * @author Joydeep
 */
public class BrowserStackAPIWrapper {

	private static final String VERSION = "4";
	private static final String ENTRY = "https://api.browserstack.com/" + VERSION;
	private static final String WORKER = "worker/%s";
	private static final String WORKER_CREATE = "worker/";
	private static final String WORKERS = "workers";
	private static final String STATUS = "status";
	private static final String SCREENSHOT = "screenshot";
	private static final String VALIDATION_ERROR = "validation error";
	private static final String UNAUTHORIZED_USER = "unauthorized user";
	private static final List<String> params =  Collections
			.unmodifiableList(Arrays.asList("os", "os_version", "browser", "device", "browser_version"));

	private String mUserName;
	private String mPassword;

	public BrowserStackAPIWrapper(String username, String password) {
		this.mUserName = username;
		this.mPassword = password;
	}

	public String make_request(String url, String type, String postDataParam) {
		switch (type) {
		case "GET":
			url = String.format("%s/%s", ENTRY, url);
			return sendGet(url);
		case "POST":
			url = String.format("%s/%s", ENTRY, url);
			return sendPost(url, postDataParam);
		case "DELETE":
			url = String.format("%s/%s", ENTRY, url);
			return sendDelete(url).toString();
		}
		return "BAD request";
	}

	private String sendGet(String url) {
		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			String userpass = this.mUserName + ":" + this.mPassword;
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", basicAuth);

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			if (responseCode == 200 || responseCode == 301) {
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			} else if (responseCode == 403 || responseCode == 422) {
				return VALIDATION_ERROR;
			} else if (responseCode == 401) {
				return UNAUTHORIZED_USER;
			}

			in.close();

			return response.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private Boolean sendDelete(String url) {

		try {
			URL obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestMethod("DELETE");
			con.connect();
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private String sendPost(String url, String urlParameters) {
		try {
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			String userpass = this.mUserName + ":" + this.mPassword;
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

			urlParameters = urlParameters + "&url=" + url;
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Authorization", basicAuth);
			con.setDoOutput(true);
			con.setInstanceFollowRedirects(false);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("charset", "utf-8");
			con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			con.setUseCaches(false);
			try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
				wr.write(postData);
				wr.close();
			}

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			if (responseCode == 200) {
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			} else if (responseCode == 403 || responseCode == 422) {
				return VALIDATION_ERROR;
			} else if (responseCode == 401) {
				return UNAUTHORIZED_USER;
			}
			in.close();

			return response.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String get_browsers(Boolean flat, Boolean all) {
		String url = "browsers";
		if (flat == true && all == true) {
			return "Wrong format";
		}
		if (flat == true) {
			url = "browsers?flat=true";

		}
		if (all == true) {
			url = "browsers?all=true";

		}

		return this.make_request(url, "GET", new String());
	}

	public String delete_worker(String id) {
		return this.make_request(String.format(WORKER, id), "DELETE", new String());
	}

	public String get_worker_with_id(String id, Boolean screenshot) {
		if (id == null) {
			return ("BAD req. Id can't be NULL");
		}
		if (screenshot == true) {
			return this.make_request(String.format(WORKER, id, SCREENSHOT), "GET", new String());
		}
		return this.make_request(String.format(WORKER, id), "GET", new String());
	}

	public String get_workers() {
		return this.make_request(WORKERS, "GET", new String());
	}

	public String get_api_status() {
		return this.make_request(STATUS, "GET", new String());
	}

	private Boolean check_element(String key, JSONObject data) {
		if (!data.containsKey("os")) {
			return false;
		}
		return true;
	}

	private Boolean check_params(JSONObject data) {
		int len = params.size();
		for (int i = 0; i < len; i++) {
			if (!this.check_element(params.get(i), data)) {
				return false;
			}
		}
		return true;
	}

	private String get_key(JSONObject data, String key) {
		String value = new String();
		return (String) data.get(key);
	}

	public String create_worker(JSONObject data) {
		String postDataParam = new String();
		// String data =

		if (!data.containsKey("os")) {
			return "INVALID REQ";
		}

		postDataParam = "os=" + this.get_key(data, "os") + "&os_version=" + this.get_key(data, "os_version") + "&device="
				+ this.get_key(data, "device") + "&browser=" + this.get_key(data, "browser") + "&browser_version="
				+ this.get_key(data, "browser_version");
		return this.make_request(WORKER_CREATE, "POST", postDataParam);
	}
}
