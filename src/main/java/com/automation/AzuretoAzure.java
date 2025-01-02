package com.automation;

/*
 * Azure to Azure automation script.
 * parse the file using JSON method and generate analysis files
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.automation.Azvars;

public class AzuretoAzure {
	static HashMap<String, String> notsupport = new HashMap<String, String>();

	static HashMap<String, String> sowresources = new HashMap<String, String>();
	static List resourcetype = new ArrayList<String>();
	Azvars az = new Azvars();

	/*
	 * Create Analysis files and SOW
	 */
	public ByteArrayOutputStream  createsow(Path uploadpath, String filename, String from, String recipient, String companyname,
						  String message, String sowpath, String endcustomer) throws IOException {
		Path folder = uploadpath;
		List<Path> tempFiles = new ArrayList<>();
		List<String> mstype1 = new ArrayList<String>();

		int nonpassflag = 0;
		int paasflag = 0;
		int planvm = 0;
		int planavailable = 0;
		int planavailablecount = 0;
		int availabilityzonevm = 0;
		int azvm = 0;
		int lbflag1 = 0;
		int pipflag = 0;
		int vnetpeer = 0;
		int classicstorageacc = 0;
		int totalsolution = 0;
		int totalworkspace = 0;
		int merakicounter = 0;
		int totalcertificates = 0;
		int totalaadds = 0;
		int totalclassicdomain = 0;
		int totalsshkeys = 0;
		int totalclassicstorage = 0;
		int totalappgway = 0;
		int totalcndfrontdoor = 0;
		int totalfrontdoor = 0;
		int totalpvtendpoints = 0;
		int totalflowlogs = 0;
		int totalexpressroutes = 0;
		int totalbastionhosts = 0;
		int totalddos = 0;
		int notplan = 0;
		int totalnatgateways = 0;
		int totalconnectiongway = 0;

		String vmwithoutplan = "";
		int totalhoursforsub = 0;
		int totalminutesforsub = 0;
		// Fetch the data from Mircosoft website for Azure move supports
		String url = az.url;
		Document document = null;
		try {
			document = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.79 Safari/537.36")
					.referrer("http://www.google.com")
					.followRedirects(true).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String text = document.select("div").first().text();
		// System.out.println(text);
		ArrayList<String> childresource = new ArrayList<String>();
		HashMap<String, ArrayList<String>> resourceslist = new HashMap<String, ArrayList<String>>();
		HashMap<String, String> resourceheader = new HashMap<String, String>();
		HashMap<String, String> doclink = new HashMap<String, String>();
		HashMap<String, String> resources = new HashMap<String, String>();
		HashMap<String, String> flags = new HashMap<String, String>();
		HashMap<String, String> planifomap = new HashMap<String, String>();
		// ArrayList<String> resources = new ArrayList<String>();
//		Elements links = document.select("ol#content-well-in-this-article-list li a");
//
//		for (Element link : links) {
//			String href = link.attr("href");
//			String heading = link.text();
//			childresource.add(heading);
//			doclink.put(text, href);
//		}
		Map<String, List<String>> supportsMigration = new HashMap<>();
		Map<String, List<String>> doesNotSupportMigration = new HashMap<>();

		Element contentDiv = document.getElementsByClass("content").first();
		if (contentDiv != null) {
			// Iterate through the elements within the content div
			Elements elements = contentDiv.children();
			String currentHeading = null;

			for (Element element : elements) {
				if (element.tagName().equals("h2")) {
					currentHeading = element.text().trim();
				} else if (element.tagName().equals("div") && element.hasClass("mx-tableFixed")) {
					Element table = element.selectFirst("table");
					if (table != null && currentHeading != null) {
						Elements rows = table.select("tbody tr");
						for (Element row : rows) {
							Elements cells = row.select("td");
							if (cells.size() >= 4) {
								String resourceType = cells.get(0).text().trim();
								String resourceGroup = cells.get(1).text().trim();
								String subscription = cells.get(2).text().trim();
								String regionMove = cells.get(3).text().trim();
								currentHeading = currentHeading.toLowerCase().replace(" ", "");
								resourceType = resourceType.toLowerCase().replace(" ", "");
								String resource = currentHeading + "/" + resourceType;
								childresource.add(currentHeading);
								doclink.put(currentHeading, currentHeading + "/" + resourceType);
								resources.put(currentHeading + "/" + resourceType, "");
								if (subscription.equalsIgnoreCase("Yes") || az.supportedAzResources.contains(resource)) {
									supportsMigration.computeIfAbsent(currentHeading, k -> new ArrayList<>()).add(resource);
								} else {
									if (!az.supportedAzResources.contains(resource)) {
										doesNotSupportMigration.computeIfAbsent(currentHeading, k -> new ArrayList<>()).add(resource);
									}

								}
								if (subscription.equalsIgnoreCase("NO")) {

									resourceheader.put(currentHeading + "/" + resourceType,
											resourceType);

									sowresources.put(currentHeading + "/" + resourceType,
											resourceType);

								}
							}
						}
					}
				}
			}
		}
		resources.put("microsoft.purview/accounts", "");


		Elements links = document.select("a");
		for (Element link : links) {
			String tt = link.attr("data-linktype");
			if (tt != "") {
				System.out.println("" + link.childNode(0));
				childresource.add(link.childNode(0).toString().toLowerCase());
				doclink.put(link.childNode(0).toString().toLowerCase(), url + link.attr("href").toLowerCase());
			}

		}

		Elements ids = document.getElementsByTag("h2");
		ArrayList<String> headerelementlist = new ArrayList<String>();
		int idcount = 0;
		for (Element header : ids) {
			if (childresource.get(idcount).equals(header.childNode(0).toString().toLowerCase())) {
				headerelementlist.add(header.childNode(0).toString().toLowerCase());
				idcount++;
			}
		}
		Elements trList = document.getElementsByTag("tr");
		int tempflag = 0;
		int count1 = 0;
		int childsize = headerelementlist.size();
		ArrayList<String> temparray = new ArrayList<String>();
		for (int i = 0; i < trList.size(); i++) {
			// System.out.println("----------------- TR START
			// -----------------");

			if (childsize != 0) {
				if (childsize == 1) {
					// System.out.println("");

				}

				if (trList.get(i).toString().contains(az.accounts)) {
					// System.out.println("in");

				}
				if (headerelementlist.get(count1).equals(childresource.get(count1))) {

					Elements tdList = trList.get(i).children();
					// if (tdList.size() == 4) {
					// String check = "";
					Elements check = tdList.get(0).getElementsByTag("td");
					if (count1 == 137) {
						// System.out.println("hii");
					}

					if (check.size() == 0) {
						temparray = new ArrayList<String>();
						count1++;
						childsize--;

					} else {
						temparray.add(tdList.get(0).childNodes().get(0).toString());
						temparray.add(tdList.get(2).childNodes().get(0).toString());
						resourceslist.put(childresource.get(count1 - 1), temparray);
						// System.out.println(childresource.get(count1-1) + "/" +
						// check.get(0).childNodes().get(0).toString() + " " +
						// tdList.get(2).childNodes().get(0).toString() + "size - " + check.size() + "
						// count- " + count1);

						if (trList.get(i).toString().contains(az.accounts) && count1 == 137 && tempflag == 0) {
							// System.out.println("hi");
							count1 = count1 - 1;
							tempflag = 1;
							// if(count1==135) {
							// count1=count1-1;
							// }

							resources.put("microsoft.purview/accounts", "");
						} else {
							resources.put(childresource.get(count1 - 1).toLowerCase() + "/"
											+ check.get(0).childNodes().get(0).toString().toLowerCase().replace(" ", "").trim(),
									"");
						}
						if (tdList.get(2).childNodes().get(0).toString().equals("No")
								|| tdList.get(2).childNodes().get(0).toString().contains("No")) {
							resourceheader.put(
									childresource.get(count1 - 1) + "/" + check.get(0).childNodes().get(0).toString(),
									tdList.get(2).childNodes().get(0).toString());

							sowresources.put(
									childresource.get(count1 - 1).toLowerCase() + "/"
											+ check.get(0).childNodes().get(0).toString().toLowerCase().replace(" ",
											""),
									check.get(0).childNodes().get(0).toString().toLowerCase().replace(" ", ""));

						}

					}

				}
			}
		}
		// map resource types to it's support/not support nature.
		for (Map.Entry<String, String> entry : resourceheader.entrySet()) {

			resourcetype.add(entry.getKey().toLowerCase());

			notsupport.put(entry.getKey().replace(" ", "").toLowerCase(),
					doclink.get(entry.getKey().split("/")[0]).replace(" ", "").toLowerCase());

		}
		// notsupport.put("sendgrid.email/accounts", "doc");

		try {
			// creates uploaded file
			// System.out.println("File Created:" + folder + filename);
			File file = uploadpath.toFile();

			// System.out.println("*****");
			// BufferedReader br = new BufferedReader(new
			// FileReader(file));

			// JSON parser object to parse read file
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader(file);

			// Read JSON file
			Object obj = jsonParser.parse(reader);
			JSONObject start = (JSONObject) obj;
			// JSONObject Subscription=(JSONObject)
			// start.get("Subscription");
			JSONArray employeeList = new JSONArray();
			employeeList.add(start.get(az.Subscriptions));
			// JSONArray employeeList = (JSONArray) obj;
			// System.out.println(employeeList);
			String TenantId = null;
			// Iterate over employee array
			HashMap<String, String> compare = new HashMap<String, String>();

			JSONArray t = null;

			t = (JSONArray) employeeList.get(0);
			// System.out.println(t);

//			checkPathAndFolder(folder);
			String textFileName = filename.split("\\.")[0];

//			String analysisfilePath = folder + "\\subAnalysis_Main\\"  ;
//			checkPathAndFolder(analysisfilePath);

            Path textFile = Files.createTempFile(textFileName, ".txt");
			tempFiles.add(textFile);
			FileWriter fw;

			fw = new FileWriter(textFile.toFile());

			for (Object ost1 : t) {

				// System.out.println(ost1);

				JSONObject Subscription = (JSONObject) ost1;

				// Get Subscription name
				String SubName = (String) Subscription.get(az.SubName);
				// System.out.println(SubName);

				// Get Subscription ID
				String SubId = (String) Subscription.get(az.SubId);
				// System.out.println(SubId);

				// Get Tenant ID
				TenantId = (String) Subscription.get(az.TenantId);
				// System.out.println(TenantId);

				// Get subscription status
				String SubStatus = (String) Subscription.get(az.SubStatus);
				// System.out.println(SubStatus);

				// parse Resource Groups
				String ResourceGroups = null;
				if (Subscription.containsKey(az.ResourceGroups)) {
					ResourceGroups = (String) Subscription.get(az.ResourceGroups);
					// System.out.println(ResourceGroups);
				}

				// parse Classic Cloud service IAAS
				String Classic_Cloud_Services_IaaS = null;
				if (Subscription.containsKey(az.Classic_Cloud_Services_IaaS)) {
					Classic_Cloud_Services_IaaS = (String) Subscription.get(az.Classic_Cloud_Services_IaaS);
					// System.out.println(Classic_Cloud_Services_IaaS);

					nonpassflag = 1;
				}
				// parse Classic Cloud service PAAS
				String Classic_Cloud_Services_PaaS = null;
				if (Subscription.containsKey(az.Classic_Cloud_Services_PaaS)) {
					Classic_Cloud_Services_PaaS = (String) Subscription.get(az.Classic_Cloud_Services_PaaS);
					// System.out.println(Classic_Cloud_Services_PaaS);
					paasflag = 1;
				}

				// parse Classic Cloud service PAAS
				Object Classic_Cloud_Services_PaaSNames = null;
				if (Subscription.containsKey(az.Classic_Cloud_Services_PaaSNames)) {
					Classic_Cloud_Services_PaaSNames = (Object) Subscription.get(az.Classic_Cloud_Services_PaaSNames);
					// System.out.println(Classic_Cloud_Services_PaaSNames);
				}

				// parse Loadbalancers
				String Load_Balancers_STANDARD_SKU = null;
				if (Subscription.containsKey(az.Load_Balancers_STANDARD_SKU)) {
					Load_Balancers_STANDARD_SKU = (String) Subscription.get(az.Load_Balancers_STANDARD_SKU);
					// System.out.println(Load_Balancers_STANDARD_SKU);

				}

				// Parse resources with public IP

				String PublicIP = null;
				if (Subscription.containsKey(az.Public_IP_Address_Count)) {
					PublicIP = (String) Subscription.get(az.Public_IP_Address_Count);
					// System.out.println(Load_Balancers_STANDARD_SKU);

				}
				/*
				 * JSONArray PublicIP = null; if (Subscription.containsKey(az.PublicIPs)) {
				 *
				 * JSONArray PublicIPlist = new JSONArray();
				 * PublicIPlist.add(Subscription.get(az.PublicIPs)); PublicIP = (JSONArray)
				 * PublicIPlist.get(0); //System.out.println(PublicIP); // iterating address Map
				 * if (PublicIP != null) { pipflag = 1; for (Object o : PublicIP) { JSONObject
				 * jsonLineItem = (JSONObject) o;
				 * System.out.println(jsonLineItem.get(az.PIP_Name));
				 * System.out.println(jsonLineItem.get(az.RG_Name)); } } }
				 */

				// Parse Vnet with peering

				int vnetpeercount = 0;
				if (Subscription.containsKey(az.vNet_Peering_Count)) {
					vnetpeercount = Integer.parseInt((String) Subscription.get(az.vNet_Peering_Count));
					if (vnetpeercount > 0)
						vnetpeer = 1;
					// System.out.println(ResourceGroups);
				}

				/*
				 * JSONArray vNetPeer = null; if (Subscription.containsKey(az.vNetPeers)) {
				 * JSONArray vNetPeerlist = new JSONArray();
				 * vNetPeerlist.add(Subscription.get(az.vNetPeers)); vNetPeer = (JSONArray)
				 * vNetPeerlist.get(0); //System.out.println(vNetPeer); vnetpeer =1; //
				 * iterating address Map if (vNetPeer != null) { for (Object o : vNetPeer) {
				 * JSONObject jsonLineItem = (JSONObject) o; //
				 * System.out.println(jsonLineItem.get(az.VNET_Name)); //
				 * System.out.println(jsonLineItem.get(az.RG_Name)); } } }
				 *
				 * /*
				 *
				 * // Parse VMs with availability zone /* JSONArray AvailabilityzoneVM = null;
				 * if (Subscription.containsKey("AvailibilityZoneVMs")) { JSONArray
				 * AvailabilityzoneVMlist = new JSONArray();
				 * AvailabilityzoneVMlist.add(Subscription.get("AvailibilityZoneVMs"));
				 * AvailabilityzoneVM = (JSONArray) AvailabilityzoneVMlist.get(0);
				 * System.out.println(AvailabilityzoneVM); if (AvailabilityzoneVM != null) { //
				 * iterating address Map azvm = 1; for (Object o : AvailabilityzoneVM) {
				 * JSONObject jsonLineItem = (JSONObject) o;
				 * System.out.println(jsonLineItem.get("VM_Name"));
				 * System.out.println(jsonLineItem.get("RG_Name"));
				 * System.out.println(jsonLineItem.get("Zone"));
				 *
				 * }
				 *
				 * } }
				 */

				// VM with disk encryptions
				int Encrypted_VM_Count = 0;
				if (Subscription.containsKey(az.Encrypted_VM_Count)) {
					Encrypted_VM_Count = Integer.parseInt((String) Subscription.get(az.Encrypted_VM_Count));

				}

				// Frontdoor instance

				int Frontdoor_Count = 0;
				if (Subscription.containsKey(az.Frontdoor_Count)) {
					Frontdoor_Count = Integer.parseInt((String) Subscription.get(az.Frontdoor_Count));

				}

				// Parse VM with Plan info
				JSONArray PlanInfoVM = null;
				String availplan = "";
				String notavailplan = "";
				if (Subscription.containsKey(az.PlanInfoVMs)) {
					JSONArray PlanInfoVMlist = new JSONArray();
					PlanInfoVMlist.add(Subscription.get(az.PlanInfoVMs));
					PlanInfoVM = (JSONArray) PlanInfoVMlist.get(0);
					// System.out.println(PlanInfoVM);
					if (PlanInfoVM != null) {
						// iterating address Map
						planvm = 1;
						for (Object o : PlanInfoVM) {
							JSONObject jsonLineItem = (JSONObject) o;
							// System.out.println(jsonLineItem.get(az.VNET_Name));
							// System.out.println(jsonLineItem.get(az.RG_Name));

							if (jsonLineItem.get(az.Plan_Available).toString().equalsIgnoreCase("yes")) {
								planavailable = 1;
								planavailablecount++;
								availplan = availplan + "'" + jsonLineItem.get(az.RG_Name).toString() + "'";
							}
							if (jsonLineItem.get(az.Plan_Product).toString().equalsIgnoreCase("cisco-meraki-vmx100")) {
								merakicounter++;

							}
							if (jsonLineItem.get(az.Plan_Available).toString().equalsIgnoreCase("no")) {
								planavailable = 0;
								vmwithoutplan = vmwithoutplan + jsonLineItem.get(az.VM_Name).toString()
										+ "does not have plan available" + "\r\n";
								notavailplan = notavailplan + "'" + jsonLineItem.get(az.VM_Name).toString() + "'";

							}
						}

					}
				}
				planifomap.put("0", notavailplan);
				planifomap.put("1", availplan);
				// Parse Resources
				JSONArray Resources = null;
				List<String> mstype = new ArrayList<String>();
				if (Subscription.containsKey(az.Resources)) {
					JSONArray Resourcelist = new JSONArray();
					Resourcelist.add(Subscription.get(az.Resources));
					Resources = (JSONArray) Resourcelist.get(0);
					// System.out.println(Resources);
					if (Resources != null) {
						// iterating resouces

						for (Object o : Resources) {
							JSONObject jsonLineItem = (JSONObject) o;
							/*
							 * System.out.println(jsonLineItem.get("Resource_Type"));
							 * System.out.println(jsonLineItem.get("Resource_Total"));
							 */
							mstype.add((String) jsonLineItem.get(az.Resource_Type).toString().toLowerCase());
							// Conditions for special types of resouces that need to taken care
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.azstore))
								classicstorageacc = classicstorageacc
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.solutions))
								totalsolution = totalsolution
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.workspace))
								totalworkspace = totalworkspace
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.certificate))
								totalcertificates = totalcertificates
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.domainservices))
								totalaadds = totalaadds
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.domainnames))
								totalclassicdomain = totalclassicdomain
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.sshkeys))
								totalsshkeys = totalsshkeys
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.azappgateway))
								totalappgway = totalappgway
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.azflowlogs))
								totalflowlogs = totalflowlogs
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.azpvtendpoints))
								totalpvtendpoints = totalpvtendpoints
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.azexpressroute))
								totalexpressroutes = totalexpressroutes
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.bastionhost))
								totalbastionhosts = totalbastionhosts
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase()
									.equals(az.ddosprotectionplans))
								totalddos = totalddos
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.natgateways))
								totalnatgateways = totalnatgateways
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());
							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase()
									.equals(az.connectiongateways))
								totalconnectiongway = totalconnectiongway
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());

							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.classicstorage))
								totalclassicstorage = totalclassicstorage
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());

							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.fdcdn))
								totalcndfrontdoor = totalcndfrontdoor
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());

							if (jsonLineItem.get(az.Resource_Type).toString().toLowerCase().equals(az.fdstandard))
								totalfrontdoor = totalfrontdoor
										+ Integer.parseInt(jsonLineItem.get(az.Resource_Total).toString());

						}

					}
				}

				// Create the folder name with company name and copy uploaded
				// file to that folder

				// Subanalysis files

				fw.write("SubName = " + SubName + "\r\n");
				fw.write("SubID = " + SubId + "\r\n");
				fw.write("TenantID = " + TenantId + "\r\n");

				fw.write("********************************* \r\n");
				if (SubStatus.equalsIgnoreCase(az.Subscription_Disabled)
						|| SubStatus.equalsIgnoreCase(az.Subscription_Has_No_Resources)) {
					fw.write("No Resources");
				} else {

					int time = 10;
					String timecalculation = "";
					double price = 187.5;
					int preptime = 1;
					time = time * Integer.parseInt(ResourceGroups);
					if (Integer.parseInt(ResourceGroups) != 0)
						timecalculation = timecalculation + "-> " + "Total Resource Group Time = " + time + "\r\n";

					preptime = preptime * Integer.parseInt(ResourceGroups);
					preptime = (preptime * 60) / 20;

					if ((preptime * 60) / 20 != 0) {
						time = time + preptime;
						timecalculation = timecalculation + "-> " + "Total Preparation Time =  " + preptime + "\r\n";
					}

					if (classicstorageacc != 0) {
						time = time + classicstorageacc * 10;
						timecalculation = timecalculation + "-> " + "Total ClassicStorage Time = "
								+ classicstorageacc * 10 + "\r\n";
					}

					// if(totalsolution!=0) {
					// time = time + totalsolution * 5;
					// timecalculation = timecalculation + "-> " +"Total Solutions Time = " +
					// totalsolution * 5 +"\r\n";

					// }

					// if(totalcertificates!=0) {
					// time = time + totalcertificates *10;
					// timecalculation = timecalculation + "-> " +"Total SSL Certificate Time = " +
					// totalcertificates * 10 +"\r\n";
					// }
					// if(totalsshkeys!=0) {
					// time = time + totalsshkeys *5;
					// timecalculation = timecalculation + "-> " +"Total totalsshkeys Time = " +
					// totalsshkeys * 5 +"\r\n";
					// }
					// if (Load_Balancers_STANDARD_SKU != null) {
					// time = time + 10 * Integer.parseInt(Load_Balancers_STANDARD_SKU);
					// timecalculation = timecalculation + "Total Load Balancer Standard SKU Time: "
					// + 10 * Integer.parseInt(Load_Balancers_STANDARD_SKU) +"\r\n";
					// }

					if (Classic_Cloud_Services_PaaS != null) {
						time = time + 30;
						timecalculation = timecalculation + "-> " + "Total Classic Cloud Service PAAS Time = " + "30"
								+ "\r\n";

					}

					if (totalaadds != 0) {
						time = time + 240;
						timecalculation = timecalculation + "-> " + "Total AADDS Instance = " + "240" + "\r\n";
					}

					/*
					 * if (PlanInfoVM != null) { time = time + PlanInfoVM.size() * 10;
					 * timecalculation = timecalculation + "Total PlanInfo VM Time: " +
					 * PlanInfoVM.size() * 10; }
					 */

					if (vnetpeercount > 0) {
						time = time + vnetpeercount * 10;
						timecalculation = timecalculation + "-> " + "Total Vnet Peering Time = " + (vnetpeercount * 10)
								+ "\r\n";
					}

					// 10 min for standard SKU Public IP addres)

					if (Classic_Cloud_Services_IaaS != null) {

						time = time + 30;
						// int nonpassflag;
						if (Classic_Cloud_Services_IaaS.toString().equalsIgnoreCase("ERROR_unable to calculate"))
							nonpassflag = 1;
						else
							nonpassflag = 2;

						timecalculation = timecalculation + "-> " + "Total Classic Cloud IAAS Time = " + "30" + "\r\n";
					}

					price = price * time;

					// Complex Deciding factors
					compare.put(az.streamanalytics.toLowerCase(), ""); // 5 Minutes
					compare.put(az.azloadbalancer.toLowerCase(), ""); // Standard
					if (totalworkspace != 0 && totalsolution != 0)
						compare.put(az.workspace.toLowerCase(), "");

					// Log analytics 5 minutes per solution
					compare.put("microsoft.recoveryservices/vaults".toLowerCase(), "");

					compare.put(az.certificate.toLowerCase(), ""); // 10 minutes
					// compare.put("Microsoft.AAD/DomainServices".toLowerCase(), ""); // 240 Minutes

					// compare.put("microsoft.visualstudio/account".toLowerCase(), "");

					// compare.put("microsoft.alertsManagement/smartDetectorAlertRules".toLowerCase(),
					// "");
					compare.put("microsoft.sql/managedinstances".toLowerCase(), "");
					compare.put(az.sshkeys.toLowerCase(), "");
					compare.put(az.azpvtendpoints.toLowerCase(), "");
					compare.put(az.azflowlogs.toLowerCase(), "");
					compare.put(az.azappgateway.toLowerCase(), "");
					compare.put(az.azexpressroute.toLowerCase(), "");
					compare.put(az.solutions.toLowerCase(), "");
					compare.put(az.bastionhost.toLowerCase(), "");
					compare.put(az.ddosprotectionplans.toLowerCase(), "");
					compare.put(az.natgateways.toLowerCase(), "");
					//compare.put(az.connectiongateways.toLowerCase(), "");
					compare.put(az.classicstorage.toLowerCase(), "");
					// compare.put(az.fdcdn.toLowerCase(),"");
					compare.put(az.fdstandard.toLowerCase(), "");

					// SQL managed instance ( 6 Hours ) ...Done

					// VM Deployed in availability Zone (20 min per instance)

					// if(PlanInfoVM!= null)
					// time = time + 20;

					// Vnet peering 10 Min per instance

					// Metric Alert .....

					String typeofmigration = "";
					for (int j = 0; j < mstype.size(); j++) {
						if (compare.containsKey(mstype.get(j)) || PlanInfoVM != null || vnetpeercount != 0
								|| PublicIP != null || Load_Balancers_STANDARD_SKU != null || PlanInfoVM != null
								|| totalaadds != 0 || Frontdoor_Count != 0) {
							typeofmigration = az.complex;
							break;
						} else {
							typeofmigration = az.simple;
							break;
						}
					}

					try {
						// Analysis File

						fw.write("Migration analysis questions:" + "\r\n");
						fw.write("1. What resources (if any) have limitations?" + "\r\n");
						// fw.wait(10);
						// watchService.poll(10,
						// TimeUnit.SECONDS);
						int classicflag = 0;
						for (int j = 0; j < mstype.size(); j++) {
							if (compare.containsKey(mstype.get(j))) {
								if (!mstype.get(j).equals(az.workspace))
									fw.write("-> " + mstype.get(j) + "\r\n");
								// time = time + 30;

								/*
								 * if(mstype.get(j).contains("Vmx100")) { time = time + 30; timecalculation =
								 * timecalculation + "Total Cisco Meraki Vmx100 Time: " + "30"; }
								 */
								if (mstype.get(j).contains("microsoft.sql/managedinstances")) {
									time = time + 360;
									timecalculation = timecalculation + "-> " + "Total Managed SQL Instance Time = "
											+ "360" + "\r\n";
									// fw.write("-> " +"Total SQL managedinstance Count = " + totalflowlogs +
									// "\r\n");
								}
								if (mstype.get(j).contains("microsoft.network/privateendpoints")) {
									int pvtendpoint = totalpvtendpoints * 5;
									time = time + pvtendpoint;
									timecalculation = timecalculation + "-> " + "Total privateendpoint Time = "
											+ pvtendpoint + "\r\n";
									fw.write("-> " + "Total Privateendpoint Count  = " + totalpvtendpoints + "\r\n");
								}
								if (mstype.get(j).contains("microsoft.network/networkwatchers")) {

									int totalwatcher = totalflowlogs * 5;
									time = time + totalwatcher;
									timecalculation = timecalculation + "-> " + "Total networkwatchers Time = "
											+ totalwatcher + "\r\n";
									fw.write("-> " + "Total Networkwatcher Count  = " + totalflowlogs + "\r\n");
								}
								if (mstype.get(j).contains("microsoft.network/applicationgateways")) {
									int appgwaycount = totalappgway * 60;
									time = time + appgwaycount;
									timecalculation = timecalculation + "-> " + "Total applicationgateways Time = "
											+ appgwaycount + "\r\n";
									fw.write("-> " + "Total Applicationgateway Count  = " + totalappgway + "\r\n");
								}
								if (mstype.get(j).contains(az.expressroute)) {
									int exproute = totalexpressroutes * 120;
									time = time + exproute;
									timecalculation = timecalculation + "-> " + "Total ExpressRoute Time = " + exproute
											+ "\r\n";
									fw.write("-> " + "Total ExpressRoute Count  = " + totalexpressroutes + "\r\n");
								}
								if (mstype.get(j).contains(az.bastionhost)) {
									int bastionhosttime = totalbastionhosts * 5;
									time = time + bastionhosttime;
									timecalculation = timecalculation + "-> " + "Total BastionHost Time = "
											+ bastionhosttime + "\r\n";
									fw.write("-> " + "Total BastionHost Count  = " + totalbastionhosts + "\r\n");
								}
								if (mstype.get(j).contains(az.ddosprotectionplans)) {
									int bastionhosttime = totalddos * 5;
									time = time + bastionhosttime;
									timecalculation = timecalculation + "-> " + "Total DDos Time = " + bastionhosttime
											+ "\r\n";
									fw.write("-> " + "Total DDos Plan Count  = " + totalddos + "\r\n");
								}

								if (mstype.get(j).contains(az.natgateways)) {
									int nattime = totalnatgateways * 30;
									time = time + nattime;
									timecalculation = timecalculation + "-> " + "Total NatGateway Time = " + nattime
											+ "\r\n";
									fw.write("-> " + "Total NatGateway Count  = " + totalnatgateways + "\r\n");
								}

								if (mstype.get(j).contains(az.sshkeys)) {
									int totalssh = totalsshkeys * 5;
									time = time + totalssh;
									timecalculation = timecalculation + "-> " + "Total SSHKeys Time = " + totalssh
											+ "\r\n";
									fw.write("-> " + "Total sshkeys Count  = " + totalsshkeys + "\r\n");
								}
								/*
								 * if(mstype.get(j).contains(az.sshkeys)) { int totalssh = totalsshkeys * 5;
								 * time = time + totalssh; timecalculation = timecalculation + "-> "
								 * +"Total SSHKeys Time = " + totalssh +"\r\n"; fw.write("-> "
								 * +"Total sshkeys Count  = " + totalsshkeys + "\r\n"); }
								 */
								if (mstype.get(j).contains(az.classicstorage)) {
									int totalclst = totalclassicstorage * 10;
									time = time + totalclst;
									timecalculation = timecalculation + "-> " + "Total Classic storage Time = "
											+ totalclst + "\r\n";
									fw.write("-> " + "Total Classic Storage Count  = " + totalclassicstorage + "\r\n");
								}

								if (mstype.get(j).contains(az.certificate)) {
									int certtime = totalcertificates * 5;
									time = time + certtime;
									timecalculation = timecalculation + "-> " + "Total Web Certificate Time = "
											+ certtime + "\r\n";
									fw.write("-> " + "Total Certificate Count  = " + totalcertificates + "\r\n");
								}
								/*
								 * if (mstype.get(j).contains(az.connectiongateways)) { int conntime =
								 * totalconnectiongway * 30; time = time + conntime; timecalculation =
								 * timecalculation + "-> " + "Total Web Conneciton Gateway Time = " + conntime +
								 * "\r\n"; fw.write("-> " + "Total Web Connection Gateway Count  = " +
								 * totalconnectiongway + "\r\n"); }
								 */

								if (mstype.get(j).contains(az.workspace)) {
									if (totalworkspace != 0 && totalsolution != 0) {
										int soltime = totalsolution * 5;
										time = time + soltime;
										timecalculation = timecalculation + "-> " + "Total Workspace Solutions Time = "
												+ soltime + "\r\n";
										fw.write("-> " + "Total Solutions = " + totalsolution);
										fw.write("\r\n");
									}

								}

								/*
								 * if(mstype.get(j).contains(az.fdcdn)) { int fdcdntime = totalcndfrontdoor *
								 * 20; time = time + fdcdntime; timecalculation = timecalculation + "-> "
								 * +"Total CDN Frontdoor Time = " + fdcdntime +"\r\n"; fw.write("-> "
								 * +"Total CDN Frontdoor Count  = " + totalcndfrontdoor + "\r\n"); }
								 */

								if (mstype.get(j).contains(az.fdstandard)) {
									int totalfdtime = totalfrontdoor * 60;
									time = time + totalfdtime;
									timecalculation = timecalculation + "-> "
											+ "Total  Frontdoor Time = " + totalfdtime + "\r\n";
									fw.write("-> "
											+ "Total  Frontdoor Count  = " + totalfrontdoor + "\r\n");

								}


							}

							if (mstype.get(j).toString().contains(az.Classic) && classicflag == 0) {
								fw.write("-> " + mstype.get(j) + "\r\n");
								time = time + 30;
								timecalculation = timecalculation + "-> " + "Total Time for Other Classic Resources = "
										+ "30" + "\r\n";
								classicflag = 1;
							}

						}

						if (vnetpeercount != 0) {
							fw.write("-> " + "Total Vnet Peering = " + vnetpeercount);
							fw.write("\r\n");

						}

						// if(totalsshkeys !=0) {
						// fw.write("-> " +"Total SSHKeys = " + totalsshkeys);
						// fw.write("\r\n");
						// }
						if (PublicIP != null) {
							fw.write("-> " + "Total PublicIP with Standard SKU  = " + PublicIP + "\r\n");
							int piptime = 1;
							piptime = 5 * Integer.parseInt(PublicIP);
							timecalculation = timecalculation + "-> " + "Total PublicIP with Standard SKU = " + piptime
									+ "\r\n";
							time = time + piptime;
						}

						if (Load_Balancers_STANDARD_SKU != null) {
							fw.write("-> " + "Total LoadBalancers with Standard SKU  = " + Load_Balancers_STANDARD_SKU
									+ "\r\n");
							int lbtime = 1;
							lbtime = 10 * Integer.parseInt(Load_Balancers_STANDARD_SKU);
							timecalculation = timecalculation + "-> " + "Total LoadBalancers with Standard SKU = "
									+ lbtime + "\r\n";
							time = time + lbtime;
						}

						if (totalaadds != 0) {
							fw.write("-> " + "Total AADDS = " + totalaadds);
							fw.write("\r\n");
						}

						if (Encrypted_VM_Count > 0) {

							int totalencryptiontime = Encrypted_VM_Count * 7;
							time = time + totalencryptiontime;
							timecalculation = timecalculation + "-> " + "Total Encrypted_VM_Count  = "
									+ totalencryptiontime + "\r\n";
							fw.write("-> " + "Total Encrypted VMs = " + Encrypted_VM_Count);
							fw.write("\r\n");
						}
						if (PlanInfoVM != null) {

							if (merakicounter != 0) {
								fw.write("-> " + "Cisco Meraki present: " + merakicounter);
								int merakitime = 1;
								merakitime = 60 * merakicounter;
								time = time + merakitime;
								timecalculation = timecalculation + "-> " + "Total Cisco Meraki Time = " + merakitime
										+ "\r\n";
								fw.write("\r\n");

							}
							int planinfovmcount = PlanInfoVM.size() - merakicounter;
							if (planinfovmcount != 0) {
								// planinfovmcount = planinfovmcount - planavailablecount
								int planvmtime = 1;
								planvmtime = planavailablecount * 10;
								fw.write("-> " + "VM with Available Plan Info : " + planinfovmcount);
								fw.write("\r\n");
								timecalculation = timecalculation + "-> " + "Total PlanInfo VM Time = " + planvmtime
										+ "\r\n";
								time = time + planvmtime;

							}
							if (planavailablecount == PlanInfoVM.size()) {
								// fw.write("-> " +"VM with Plan Info are available in Target subscription");
								// fw.write("\r\n");

								planavailable = 1;
							}

							if (planavailablecount != PlanInfoVM.size()) {
								notplan = PlanInfoVM.size() - planavailablecount;

								if (notplan == 0) {
									planavailable = 0;
								} else {
									planavailable = 2;
								}


							}
						}
						// Planinfo VM check ends
						// Section 2 starts

						fw.write("2. What resources (if any) are not supported for migration?" + "\r\n");

						for (int j = 0; j < mstype.size(); j++) {
							if (notsupport.containsKey(mstype.get(j)) && !compare.containsKey(mstype.get(j))) {
								fw.write("-> " + mstype.get(j) + "\r\n");

							}

							if (mstype.get(j).contentEquals(az.fdcdn)) {
								fw.write("-> " + mstype.get(j) + "\r\n");
							}

						}


						Set<String> keys = resources.keySet();
						List<String> notsupportedlist = new ArrayList<String>();
						for (String key : keys) {
							notsupportedlist.add(key.trim());

						}



						/*
						 * for (int index= 0; index < mstype.size(); index++) {
						 * if(!resources.containsKey(mstype.get(index).toString().trim())){
						 * fw.write("-> " +mstype.get(index) +
						 * "- This Resource type is not listed on the Azure Resource Page" + "\r\n");
						 * flags.put(mstype.get(index), "0"); }
						 *
						 * }
						 */
						for (int index = 0; index < mstype.size(); index++) {
							int fl = 0;
							for (int idex = 0; idex < notsupportedlist.size(); idex++) {

								if (mstype.get(index).toString().trim().equals("microsoft.recoveryservices/vaults")) {
									//System.out.println("test");
								}
								if (notsupportedlist.get(idex).toString().trim()
										.equals("microsoft.recoveryservices/vaults")) {
									//System.out.println("test1");
								}
								if (az.supportedAzResources.contains(mstype.get(index).trim())) {
									fl = 1;
								}
								if (notsupportedlist.get(idex).toString().trim()
										.equals(mstype.get(index).toString().trim())) {

									// System.out.println(mstype.get(index));
									// fw.write("-> " +mstype.get(index) + "- This Resource type is not listed on
									// the Azure Resource Page" + "\r\n");
									// flags.put(mstype.get(index), "0");
									fl = 1;
								}
								// System.out.println(notsupportedlist.get(idex) +"notsupportedlist.get(idex)");
								// System.out.println(mstype.get(index).toString().trim()
								// +"mstype.get(index).toString().trim())");

							}
							if (fl == 0) {

								fw.write("-> " + mstype.get(index)
										+ " - This Resource type is not listed on the Azure Resource Page" + "\r\n");
								flags.put(mstype.get(index), "0");

							}

						}
						// Planinfo VM check starts


						// Condition for classic cloud
						if (totalclassicdomain != 0 && Classic_Cloud_Services_IaaS != null && nonpassflag != 1) {
							if (totalclassicdomain > Integer.parseInt(Classic_Cloud_Services_IaaS)) {
								fw.write(
										"Total Microsoft.ClassicCompute/domainNames is greater than Classic_Cloud_Services_IAAS");
								fw.write("\r\n");
							}
						}
						if (notplan != 0)
							fw.write("-> " + "-> VM with UNAvailable Plan Info   = " + notplan + "\r\n");

						fw.write("3. What type of migration -- simple or complex?" + "\r\n");
						fw.write("-> " + typeofmigration + "\r\n");

						fw.write("4. What is your time estimate to complete the migration?" + "\r\n");

						int timeinminute = time % 60;
						int mod = timeinminute % 15;
						int res = 0;
						if ((mod) >= 8) {
							res = timeinminute + (15 - mod);
						} else {
							res = timeinminute - mod;
						}

						if (res == 60) {
							time = time + 60;
							res = 0;
						}

						fw.write(timecalculation + "\r\n");
						fw.write("-> " + time / 60 + "hours" + " and " + res + "minutes" + "\r\n");
						totalhoursforsub = totalhoursforsub + (time / 60);
						totalminutesforsub = totalminutesforsub + res;

					} catch (Exception e) {
						System.out.println(e);
					}
				}

// reset all variables
				mstype1.addAll(mstype);
				mstype.clear();
				classicstorageacc = 0;
				totalworkspace = 0;
				totalsolution = 0;
				totalcertificates = 0;
				merakicounter = 0;
				totalaadds = 0;
				planavailablecount = 0;
				planavailable = 0;
				totalsshkeys = 0;
				totalaadds = 0;
				totalappgway = 0;

				totalclassicdomain = 0;
				totalworkspace = 0;
				totalexpressroutes = 0;
				totalflowlogs = 0;
				totalpvtendpoints = 0;
				totalsolution = 0;
				totalworkspace = 0;
				merakicounter = 0;
				totalcertificates = 0;
				totalaadds = 0;
				totalclassicdomain = 0;
				totalsshkeys = 0;
				totalappgway = 0;
				totalpvtendpoints = 0;
				totalflowlogs = 0;
				totalexpressroutes = 0;
				totalbastionhosts = 0;
				totalddos = 0;
				totalclassicstorage = 0;
				totalnatgateways = 0;
				totalfrontdoor = 0;
				totalcndfrontdoor = 0;
				vnetpeer = 0;
				Encrypted_VM_Count = 0;
				Frontdoor_Count = 0;
				// PublicIP=0;
				fw.write("\r\n");
				fw.write("\r\n");
			}
			fw.write("************************************************************************************");
			fw.write("\r\n");
			totalhoursforsub = totalhoursforsub + (totalminutesforsub / 60);
			totalminutesforsub = totalminutesforsub % 60;
			fw.write("----------> Total Time for All Subscription(s) = " + totalhoursforsub + " Hours and "
					+ totalminutesforsub + "minutes <----------");
			fw.close();
			// SOW write
			try {
//				checkPathAndFolder(sowpath);
//				String filePath = sowpath + File.separator + "SOW.docx";

//				File sowFile = new File(filePath);
//				createFileIfDoesntExist(sowFile);
//				Path tempSowFile = Files.createTempFile("SOW", ".docx");
//				tempFiles.add(tempSowFile);
//				FileOutputStream  is = new FileOutputStream(tempSowFile.toFile());
//				XWPFDocument doc = new XWPFDocument(is);
//
//				List<XWPFParagraph> paras = doc.getParagraphs();
				XWPFDocument newdoc = new XWPFDocument();

//				for (XWPFParagraph para : paras) {
//
//					if (!para.getParagraphText().isEmpty()) {
//						XWPFParagraph newpara = newdoc.createParagraph();
//						newpara.setNumID(para.getNumID());
//						copyAllRunsToAnotherParagraph(para, newpara);
//					}
//
//				}
				XWPFParagraph thingstonote = newdoc.createParagraph();
				XWPFRun run = thingstonote.createRun();

				// Variables to check if any resource with not repeat while writing SOW
				mstype1 = (List) mstype1.stream().distinct().collect(Collectors.toList());
				int certflag = 0;
				int classic1flag = 0;
				int classic2flag = 0;
				int classic3flag = 0;
				int ciscoflag = 0;
				int datafactflag = 0;
				int devopsflag = 0;
				int exprouteflag = 0;
				int wsflag = 0;
				int lbflag = 0;
				int migrateflag = 0;
				int rsvflag = 0;
				int sendgridflag = 0;
				int streamjobflag = 0;
				int visflag = 0;
				int insflag = 0;
				int pipstandardflag = 0;
				int planifoflag = 0;
				int azvmflag = 0;
				int aaddsflag = 0;
				int vnetpflag = 0;

				for (int j = 0; j < mstype1.size(); j++) {

					if (mstype1.get(j).toString().contains(az.certificates) && certflag == 0) {
						run.addBreak();

						run.setText(
								"Certificates - Microsoft doesn�t support migrating Web apps that have uploaded (i.e., third-party) SSL certificates.  The Service Provider will unbind and delete the certificates, which will allow the resources to migrate.  The Customer or End Customer will need to reupload and bind the certificates.  This will require downtime ");
						certflag = 1;
					} else if (mstype1.get(j).toString().contains(az.classic) && paasflag == 1 && classic1flag == 0) {
						run.addBreak();

						run.setText(
								"Classic Cloud Services (Web/Worker Role)  - As documented at https://docs.microsoft.com/en-us/azure/virtual-machines/windows/migration-classic-resource-manager-overview?toc=%2fazure%2fvirtual-machines%2fwindows%2ftoc.json#unsupported-features-and-configurations , Microsoft does not support upgrading Cloud services that contain web/worker roles.  The Customer or End Customer will need to deploy new resources and publish the content to the new resources.  This article https://contos.io/moving-from-old-school-azure-cloud-services-to-v2-azure-paas-cbf1e93547e5 provides some suggested solutions. ");
						classic1flag = 1;
						paasflag = 1;
					} else if (mstype1.get(j).toString().contains(az.classic) && nonpassflag == 1
							&& classic2flag == 0) {
						run.addBreak();

						run.setText(
								" Classic Cloud Services �ERROR_unable to calculate� � The Discovery Script detected Classic Cloud Services, but it was unable to evaluate the resources.  This is most likely due to the user account running the script not having the Co-Administrator role.  See this link https://docs.microsoft.com/en-us/azure/role-based-access-control/rbac-and-directory-admin-roles#classic-subscription-administrator-roles for an explanation of the role and where in the Azure Portal to set the role.  If you grant a user account the Co-Administrator role, please close the PowerShell session, reopen, and rerun the Discovery script to see if that clears the error");
						classic2flag = 1;
						nonpassflag = 0;
					} else if (mstype1.get(j).toString().contains(az.classic) && nonpassflag == 2
							&& classic3flag == 0) {
						run.addBreak();

						run.setText(
								"Classic Cloud Services  - The subscription contains one or more Classic Cloud Services.  The Service Provider will need to perform validation tasks and potential remediation on the underlying VM(s) and/or network(s) to get them upgraded to Azure Resource Manager (ARM).  This could include disabling backup jobs, removing unsupported VM extensions, removing ACL endpoints, deleting unattached disks, etc.  If the Cloud Services in the source subscription are connected to a Classic Virtual Network, the VMs will remain running, and there will be no downtime.\r\n"
										+ "\r\n"
										+ "If the Cloud Services in the source subscription are not connected to a Classic Virtual Network, the VMs will need to be powered down when converting them to ARM.  This will require downtime during the upgrade and migration to the target subscription\r\n"
										+ " ");
						// classiciaasflag =1;
						nonpassflag = 0;
						classic3flag = 1;

					} else if (mstype1.get(j).toString().contains(az.vMX100) && ciscoflag == 0) {
						run.addBreak();

						run.setText(
								"Cisco Meraki vMX100 - Cisco creates a read-only lock on the resource group where the appliance (VM) resides, which prevents it migrating to a new subscription.   \r\n"
										+ " \r\n"
										+ "Also, the vMX100 is not available in the Marketplace Gallery for CSP subscriptions, but Cisco posted manual deployment guidance at https://documentation.meraki.com/MX/Installation_Guides/vMX100_Setup_Guide_for_Microsoft_Azure#vMX100_on_Azure_CSP_Portal . \r\n"
										+ " \r\n"
										+ "The Service Provider will work with the Customer and/or End Customer to deploy a new vMX100 appliance in the target subscription\r\n"
										+ " ");
						run.setBold(false);
						ciscoflag = 1;

					} else if (mstype1.get(j).toString().contains("microsoft.dataFactory") && datafactflag == 0) {
						run.addBreak();

						run.setText(
								"Data Factories - These resources are supported for migration, but there is an undocumented limitation that SSIS-IRs cannot be in a starting/started/stopping state and they cannot be self-hosted.  If the Service Provider detects the presence of SSIS-IRs, we will attempt to stop them so the migration can continue and restart after the migration.  If any of the SSIS-IRs are self-hosted, the Customer or End Customer will need to delete them prior to the migration and recreate them after the migration is complete ");
						datafactflag = 1;
					} else if (mstype1.get(j).toString().contains("microsoft.devops") && devopsflag == 0) {
						run.addBreak();

						run.setText(
								"DevOps � DevOps resources are tied to the billing subscription, which means they are not part of the migration process.  The Customer or End Customer will need to follow guidance in the following document to update the billing subscription to the new subscription https://docs.microsoft.com/en-us/azure/devops/organizations/billing/change-azure-subscription?toc=%2Fazure%2Fazure-resource-manager%2Ftoc.json&view=azure-devops");
						devopsflag = 1;
					} else if (mstype1.get(j).toString().contains(az.expressroute) && exprouteflag == 0) {
						run.addBreak();

						run.setText(
								"ExpressRoute - As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/move-support-resources#microsoftnetwork, Microsoft does not support migrating anything related to ExpressRoute.  The End Customer will need to configure a new connection in the target subscription.  Creating a new ExpressRoute Circuit requires the End Customer to coordinate the request with their local TelCo provider for the MPLS circuit, and this can take a days to weeks to facilitate.  There will be downtime to switch over the existing connection to the new subscription ");
						exprouteflag = 1;
					} else if (mstype1.get(j).toString().contains("microsoft.operationalinsights/workspaces")
							|| mstype1.get(j).toString().contains("microsoft.operationsmanagement/solutions")
							&& wsflag == 0) {
						run.addBreak();

						run.setText(
								"Log Analytics (Workspace and Solutions)  - The Service Provider will take a best-effort approach to migrating these resources.  This includes deleting the solutions and reinstalling them in the target CSP subscription.  Worst-case the Customer or End Customer may need to create a new Log Analytics Workspace and/or solutions ");
						wsflag = 1;
					} else if (mstype1.get(j).toString().contains(az.loadbalancers) && lbflag1 == 1 && lbflag == 0) {
						run.addBreak();

						run.setText(
								"Load Balancers - As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#services-that-can-be-moved , Load Balancers with the Standard SKU cannot be migrated.  The Service Provider will document the settings, delete the Load Balancer, and redeploy the Load Balancer in the target subscription.  This will require downtime ");
						lbflag = 1;
					} else if (mstype1.get(j).toString().contains("microsoft.migrate/projects") && migrateflag == 0) {
						run.addBreak();

						run.setText(
								"Migrate Project - As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/move-support-resources#microsoftmigrate , Microsoft doesn�t support migrating this resource type.  The Customer or End Customer can rerun the Azure Migrate tool to recreate the project in the CSP subscription if needed ");
						migrateflag = 1;
					} else if (mstype1.get(j).toString().contains("microsoft.network/publicipaddresses") && pipflag == 1
							&& pipstandardflag == 0) {
						run.addBreak();

						run.setText(
								"Public IP Address Standard SKU � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/move-support-resources#microsoftnetwork , Microsoft doesn�t support migrating public IP addresses that are deployed under the Standard SKU.  The Service Provider will need to delete the public IP address, migrate the resource, and create a new public IP address.  This will require reconfiguration and potential downtime");
						pipstandardflag = 1;
						pipflag = 0;
					} else if (mstype1.get(j).toString().contains("microsoft.recoveryservices/vaults") && rsvflag == 0) {
						run.addBreak();

						run.setText(
								"Recovery Services Vaults - As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#microsoftrecoveryservices Microsoft has some limitations on migrating Recovery Services Vaults.  The Service Provider will take a best effort approach, but cannot guarantee the vault will migrate, as the discovery script output cannot determine if the vault has unsupported migration items (e.g., Azure Files, Azure File Sync, SQL in IaaS VMs, Azure Site Recovery, etc.).  Worst case scenario, the Customer or End Customer will need to create new vault(s) in the target subscription(s) and enable protection.  There is no downtime required ");
						rsvflag = 1;
					}
					/*
					 * else if (mstype1.get(j).toString().contains("sendgrid") && sendgridflag == 0)
					 * { run.addBreak();
					 *
					 * run.setText(
					 * "SendGrid - As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/move-support-resources#third-party-services , Microsoft does not support migrating third-party resource providers, which includes SendGrid. The Customer or End Customer will need to create a new SendGrid account and configure any systems/apps to use the new service.  There will be downtime during the transition, although this can be minimized somewhat by pre-creating the SendGrid account on the target subscription"
					 * ); sendgridflag = 1; }
					 */

					else if (mstype1.get(j).toString().contains("microsoft.streamanalytics/streamingjobs")
							&& streamjobflag == 0) {
						run.addBreak();

						run.setText(
								"Streaming Analytics Jobs - These are now supported for migration; however, the jobs cannot be running while migrating to a new subscription.  The Service Provider, Customer, and/or End Customer will need to stop any running jobs prior to the migration.  The jobs can be restarted without losing any data after migrating to the target CSP subscription ");
						streamjobflag = 1;
					} else if (mstype1.get(j).toString().contains("microsoft.visualstudio") && visflag == 0) {
						run.addBreak();

						run.setText(
								"DevOps �Azure DevOps organizations will automatically move once someone within in the organizations updates it to point to the new CSP subscription.  See this link https://docs.microsoft.com/en-us/azure/devops/organizations/billing/change-azure-subscription?toc=%2Fazure%2Fazure-resource-manager%2Ftoc.json&view=azure-devops .  In addition to updating the billing subscription, a developer will need to go in and update pipelines to point to the new subscription.\r\n"
										+ "�	To change or remove your billing subscription, you must be a member of the Project Collection Administrators group or be the organization Owner.\r\n"
										+ "�	To change your Azure billing subscription, you must be added as an Owner or Contributor to an Azure subscription that you can use to purchase.\r\n"
										+ "");
						visflag = 1;
					} else if (mstype1.get(j).toString().contains("microsoft.alertsmanagement/smartdetectoralertrules")
							&& insflag == 0) {
						run.addBreak();
						run.setText(
								"Smartdetectoralertrules - These are Application Insights alerts (e.g., forbidden requests, failure anomalies, etc.)  The Service Provider will take a best effort approach to migrate these resources; however, if these fail, the alerts should be automatically regenerated in the target subscription based on telemetry data.  This document provides an overview of the alerts - https://docs.microsoft.com/en-us/azure/azure-monitor/app/proactive-performance-diagnostics .");

						insflag = 1;
					}

					/*
					 * else if
					 * (mstype1.get(j).toString().contains("microsoft.compute/virtualmachines") &&
					 * planifoflag == 0 && planvm == 1) { run.addBreak(); run.setBold(true); if
					 * (planavailable == 1) { run.setText(
					 * "VMs with AVAILABLE �plan info� � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#virtual-machines-limitations , Microsoft doesn�t support migrating this type of VM.  This VM uses a third-party OS image, and it requires acceptance of Marketplace terms before we can redeploy into a new subscription.  The process calls for documenting the plan info, deleting the VM object (not the disks), migrating the remaining resources, and deploying the VM in the target subscription from the migrated disks.  In some cases (e.g., Cisco ASA and Palo Alto), the licensing did not carry over to the rebuilt VM.  The End Customer will need to have an active support contract with the vendor from which the VM images were deployed in case they need to contact support and ask them to rekey/relicense the rebuilt VMs.This will require downtime."
					 * ); } if (planavailable == 0) { run.setText(
					 * "VMs with UNAVAILABLE �plan info� � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#virtual-machines-limitations , Microsoft doesn�t support migrating this type of VM.  This VM uses a third-party OS image; however, the vendor no longer offers the image in the Azure Marketplace Gallery.  The Customer or End Customer will need to remove the old VM from the source subscription and deploy a new VM in the target subscription that uses an updated image from the Marketplace Gallery.  This will require downtime"
					 * );
					 *
					 * } if(planavailable ==2) { run.setText(
					 * "VMs with AVAILABLE �plan info� � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#virtual-machines-limitations , Microsoft doesn�t support migrating this type of VM.  This VM uses a third-party OS image, and it requires acceptance of Marketplace terms before we can redeploy into a new subscription.  The process calls for documenting the plan info, deleting the VM object (not the disks), migrating the remaining resources, and deploying the VM in the target subscription from the migrated disks.  In some cases (e.g., Cisco ASA and Palo Alto), the licensing did not carry over to the rebuilt VM.  The End Customer will need to have an active support contract with the vendor from which the VM images were deployed in case they need to contact support and ask them to rekey/relicense the rebuilt VMs.This will require downtime."
					 * ); run.setText(
					 * "VMs with UNAVAILABLE �plan info� � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#virtual-machines-limitations , Microsoft doesn�t support migrating this type of VM.  This VM uses a third-party OS image; however, the vendor no longer offers the image in the Azure Marketplace Gallery.  The Customer or End Customer will need to remove the old VM from the source subscription and deploy a new VM in the target subscription that uses an updated image from the Marketplace Gallery.  This will require downtime"
					 * );
					 *
					 * }
					 *
					 * planifoflag = 1; }
					 */
					/*
					 * else if
					 * (mstype1.get(j).toString().contains("microsoft.compute/virtualmachines") &&
					 * azvmflag == 0 && azvm == 1) { run.addBreak(); run.setText(
					 * "VMs deployed in an Availability Zone � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/move-limitations/virtual-machines-move-limitations , Microsoft doesn�t support migrating VMs deployed in an availability zone.  The Service Provider will copy the managed disks to the target subscription, delete the VM object, migrate remaining resources, and redeploy the VM from the copied disks. This will require downtime"
					 * ); azvmflag = 1; }
					 */

					else if (mstype1.get(j).toString().contains("microsoft.compute/restorepointcollections")
							|| mstype1.get(j).toString().contains("restorepointcollections / restorepoints")) {

					} else if (mstype1.get(j).toString().contains("microsoft.aad/domainservices")) {
						run.addBreak();
						run.setText(
								"Azure AD Domain Services � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/move-support-resources#microsoftaad , Microsoft does not support migrating this type of resource to another subscription.  The Service Provider will manually recreate users, rejoin VMs to the domain, and recreate policies in the new subscription.  There will be downtime while the Service Provider recreates the Azure AD Domain Services.");
						aaddsflag = 1;
					} else {
						if (notsupport.containsKey(mstype1.get(j))) {
							// Resources with Link
							run.addBreak();
							run.setBold(true);
							run.setText(sowresources.get(mstype1.get(j)));
							run.setText("");
							run.setBold(false);
							run.setText(" - As Documented at " + notsupport.get(mstype1.get(j))
									+ ", Microsoft does not support migrating this type of resource to another subscription. ");
							run.setText(
									" The Customer or End Customer will need to recreate this service on the new subscription.  There will be downtime while the Customer or End Customer recreates the "
											+ sowresources.get(mstype1.get(j)) + "\r");

						}

					}
				}
				if (flags.size() != 0) {
					for (Map.Entry<String, String> entry : flags.entrySet()) {
						run.addBreak();
						run.setText(entry.getKey()
								+ "-- This resource type  is not listed on the Azure resource page found at https://docs.microsoft.com/en-us/azure/azure-resource-manager/management/move-support-resources.  The Service Provider cannot determine if this is supported for migration.  Worst case, the Customer or End Customer will need to recreate this resource type.");

					}
				}

				if (vnetpeer == 1) {
					run.addBreak();
					run.setText(
							"vNet Peering -  Microsoft does not support migrating virtual networks where peering is enabled. The Service provider will document the existing peering configuration, delete the peering connection, migrate the networks, and recreate the peering connection. This will require downtime.");

				}

				for (String key : planifomap.keySet()) {
					String value = planifomap.get(key);
					if (key == "1" && value != null && !value.equalsIgnoreCase("")) {
						run.addBreak();

						run.setText(
								"VMs with AVAILABLE �plan info� � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#virtual-machines-limitations , Microsoft doesn�t support migrating this type of VM.  This VM uses a third-party OS image, and it requires acceptance of Marketplace terms before we can redeploy into a new subscription.  The process calls for documenting the plan info, deleting the VM object (not the disks), migrating the remaining resources, and deploying the VM in the target subscription from the migrated disks.  In some cases (e.g., Cisco ASA and Palo Alto), the licensing did not carry over to the rebuilt VM.  The End Customer will need to have an active support contract with the vendor from which the VM images were deployed in case they need to contact support and ask them to rekey/relicense the rebuilt VMs.This will require downtime.");

					}
					if (key == "0" && value != null && !value.equalsIgnoreCase("")) {
						run.addBreak();

						run.setText(
								"VMs with UNAVAILABLE �plan info� � As documented at https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-move-resources#virtual-machines-limitations , Microsoft doesn�t support migrating this type of VM.  This VM uses a third-party OS image; however, the vendor no longer offers the image in the Azure Marketplace Gallery.  The Customer or End Customer will need to remove the old VM from the source subscription and deploy a new VM in the target subscription that uses an updated image from the Marketplace Gallery.  This will require downtime");

					}

				}

				String filenameforsow = "";
//				checkPathAndFolder(folder +"\\" +endcustomer );


//				filenameforsow = folder +"\\" +endcustomer +"\\"+ filename + ".docx";
				Path tempFilenameforsow = Files.createTempFile(filename.split("\\.")[0], ".docx");
				tempFiles.add(tempFilenameforsow);
//				File fileSowFile = new File(tempFilenameforsow.toFile());
//				createFileIfDoesntExist(fileSowFile);
				FileOutputStream fos = new FileOutputStream(tempFilenameforsow.toFile());
				newdoc.write(fos);
				fos.flush();
				fos.close();
				newdoc.close();

				flags.clear();

				// Create ZIP stream for all temp files
				ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
				try (ZipOutputStream zos = new ZipOutputStream(zipStream)) {
					for (Path tempFile : tempFiles) {
						zos.putNextEntry(new ZipEntry(tempFile.getFileName().toString()));
						Files.copy(tempFile, zos);
						zos.closeEntry();
					}
				}

				return zipStream;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return null;
	}

	private void checkPathAndFolder(String folder) throws IOException {
		Path path = Paths.get(folder);

		if (!Files.exists(path)) {

			Files.createDirectory(path);
			// System.out.println("Directory created");
		} else {

			// System.out.println("Directory already exists");
		}
	}

	public void sendzip(ByteArrayOutputStream zipped, String endcustomer, String from, String companyname, String message) throws IOException {


		MyEmailer test = new MyEmailer();
		try {
			test.SendMail(zipped, from, companyname, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Success...");

		// resources.clear();
	}

	public void sendzip(String uploadpath, String endcustomer, String from, String companyname, String message) throws IOException {
		checkPathAndFolder(uploadpath);
		String zipfile = uploadpath.substring(0, uploadpath.length() - endcustomer.length()) + endcustomer + ".zip";
		try {
			// Create the output ZIP file
			try (FileOutputStream fos = new FileOutputStream(zipfile);
				 ZipOutputStream zos = new ZipOutputStream(fos)) {
				Path basePath = Paths.get(uploadpath);
				// Use Files.walk to get a stream of all files and directories
				try (Stream<Path> paths = Files.walk(Paths.get(uploadpath))) {

					// Filter files that are regular files and end with .docx or .txt
					paths.filter(path -> path.toString().endsWith(".docx") || path.toString().endsWith(".txt"))
							.forEach(path -> zipFile(basePath, path, zos));

				}

			}

			System.out.println("Zip file created successfully: " + zipfile);

		} catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe);
			ioe.printStackTrace();
		}

		MyEmailer test = new MyEmailer();
		try {
			test.SendMail(zipfile, from, companyname, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Success...");

		// resources.clear();
	}

	private static void zipFile(Path basePath, Path file, ZipOutputStream zos) {
		try {
			String zipEntryName = basePath.relativize(file).toString().replace("\\", "/");
			// Create a new ZipEntry for each file with relative path
			zos.putNextEntry(new ZipEntry(zipEntryName));

			// Use Files.newInputStream to read the file's content
			try (InputStream is = Files.newInputStream(file)) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
			}

			// Close the current entry
			zos.closeEntry();
			System.out.println("Added file: " + file.toString());

		} catch (IOException e) {
			System.out.println("Error adding file to zip: " + file.toString());
			e.printStackTrace();
		}
	}
void createFileIfDoesntExist(File sowFile) throws IOException {
	if (!sowFile.exists()) {
		System.out.println("File does not exist. Creating a blank SOW.docx file...");

		// Create any missing directories first
		sowFile.getParentFile().mkdirs();

		// Create a blank Word document
		sowFile.createNewFile();

		// Optionally, create a blank Word document using XWPFDocument
		try (XWPFDocument blankDoc = new XWPFDocument();
			 FileOutputStream out = new FileOutputStream(sowFile)) {
			blankDoc.write(out);
			System.out.println("Blank SOW.docx file created successfully.");
		}
	}
}
	void copyAllRunsToAnotherParagraph(XWPFParagraph oldPar, XWPFParagraph newPar) {
		final int DEFAULT_FONT_SIZE = 10;

		for (XWPFRun run : oldPar.getRuns()) {
			String textInRun = run.getText(0);
			if (textInRun == null || textInRun.isEmpty()) {
				continue;
			}

			int fontSize = run.getFontSize();
			System.out.println("run text = '" + textInRun + "' , fontSize = " + fontSize);

			XWPFRun newRun = newPar.createRun();

			// Copy text
			newRun.setText(textInRun);

			// Apply the same style
			newRun.setFontSize((fontSize == -1) ? DEFAULT_FONT_SIZE : run.getFontSize());
			newRun.setFontFamily(run.getFontFamily());
			newRun.setBold(run.isBold());
			newRun.setItalic(run.isItalic());
			newRun.setStrike(run.isStrike());
			newRun.setColor(run.getColor());
		}
	}

}



