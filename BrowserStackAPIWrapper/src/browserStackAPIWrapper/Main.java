package browserStackAPIWrapper;

import org.json.simple.JSONObject;

public class Main {

	public static void main(String[] args) throws Exception {
		
		JSONObject params= new JSONObject();
		params.put("os", "Windows");
		params.put("os_version", "XP");
		params.put("device", "Nokia Lumia 930");
		params.put("browser", "opera");
		params.put("browser_version", "10.6");
		
		System.out.println(params.toString());

		BrowserStackAPIWrapper http = new BrowserStackAPIWrapper("joydeepmondal1","W5Gt4YqyMJHTNxiEzpnv");
		// System.out.println(http);
		// System.out.println(http.get_browsers(false,false).toString());
		// System.out.println(http.get_api_status().toString());
		// System.out.println(http.get_workers().toString());
		// System.out.println(http.get_worker_with_id("1",true).toString());
		// System.out.println(http.delete_worker("1").toString());
		// System.out.println(http.create_worker(params).toString());
		System.out.println(http.delete_worker("82708698").toString());
		

		

	}

}
