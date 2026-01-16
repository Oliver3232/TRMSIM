/**
 * "TRMSim-WSN, Trust and Reputation Models Simulator for Wireless
 * Sensor Networks" is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version always keeping
 * the additional terms specified in this license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package es.ants.felixgm.trmsim_wsn.trm.templatetrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import es.ants.felixgm.trmsim_wsn.network.Sensor;
import es.ants.felixgm.trmsim_wsn.network.Service;
import es.ants.felixgm.trmsim_wsn.outcomes.Outcome;
import es.ants.felixgm.trmsim_wsn.satisfaction.SatisfactionDiscreteScale;
import es.ants.felixgm.trmsim_wsn.satisfaction.SatisfactionInterval;
import es.ants.felixgm.trmsim_wsn.trm.GatheredInformation;

/**
 * <p>
 * This class models a Sensor implementing TemplateTRM
 * </p>
 *
 * @author <a href="http://ants.dif.um.es/~felixgm/en"
 * target="_blank">F&eacute;lix G&oacute;mez M&aacute;rmol</a>, <a
 * href="http://webs.um.es/gregorio" target="_blank">Gregorio
 * Mart&iacute;nez P&eacute;rez</a>
 * @version 0.3
 * @since 0.3
 */
public class TemplateTRM_Sensor extends Sensor {

	/**
	 * This constructor creates a new Sensor implementing TemplateTRM
	 *
	 */
	TemplateTRM_Network platform;
	public HashMap<String, Double> servicesGoodness2;
	protected HashMap<String, Application_struct> friends;
	protected static int _windowSize; /* small windows for detection */
	boolean malicious = false;

	public TemplateTRM_Sensor(TemplateTRM_Network Platform) {
		super();
		friends = new HashMap<String, Application_struct>();
		servicesGoodness2 = new HashMap<String, Double>();
		this.platform = Platform;
		friends.put("recommendation", new Application_struct("recommendation",
				Math.random() * 0.2 + 0.8));

	}

	/**
	 * This constructor creates a new Sensor implementing TemplateTRM
	 *
	 * @param id
	 * Identifier of the new sensor
	 * @param x
	 * X coordinate of the new sensor
	 * @param y
	 * Y coordinate of the new sensor
	 */
	public TemplateTRM_Sensor(int id, double x, double y,TemplateTRM_Network Platform) {
		super(id, x, y);
		friends = new HashMap<String, Application_struct>();
		servicesGoodness2 = new HashMap<String, Double>();
		this.platform = Platform;
		friends.put("recommendation", new Application_struct("recommendation",
				Math.random() * 0.5 + 0.5));
	}

	/**
	 * This method adds a new requestedService to the collection of shares of
	 * this sensor
	 *
	 * @param name
	 * the id of the service
	 * */

	public synchronized void addNewService(String name) {

		friends.put(name, new Application_struct(name,
				Math.random() * 0.5 + 0.5));
	}

	public synchronized void addfollowee(TemplateTRM_Sensor followee,
										 String service,double trust) {
		if (followee.id!=this.id)
		{
			this.addLink(followee);

			friends.get(service).followee_put(followee,trust);
			friends.get("recommendation").followee_put(followee,0.51);}
	}

	public synchronized Set<String> getRequestedServices() {

		return friends.keySet();
	}

	@Override
	public void run() {
		outcome = null;
		Set<String> Services = getRequestedServices();
		Set<String> my=  servicesGoodness2.keySet();
		for (String currServ:my)
			if ((servicesGoodness2.get("My service")!=null ) && (servicesGoodness2.get("My service")< 0.5)) malicious=true;
		for (String s : Services)// gia kathe service pou thelw
		{
			if (s.equals("My service")) {
				Application_struct new_requiredService = get_app(s);
				outcome = new_requiredService.request_Service(); // request service base on Trust on followees
				if (outcome == null)
				{ if (Math.random() < 0.3 ) outcome=get_assistance(s,5,this.id);
				else outcome=get_recommendation(s);
				}
				if (outcome == null)
				{  TemplateTRM_Sensor recommended=platform.get_recommendation(s);
					if (recommended!=null){
						addfollowee(recommended,s,0.9);
						outcome = new_requiredService.request_Service();
					}
				}
			}
			if (Math.random() <0.1) { this.report();
				this.blacklist();
			}
		}
	}

	private synchronized void blacklist() {
		Set<String> Services = getRequestedServices();

		for (String s : Services)// gia kathe service pou thelw
		{
			if (s.equals("My service")) {

				Application_struct checkedService = friends.get(s);
				PriorityQueue<ComparableFriend> MyQueue=checkedService.rank_friends("trust");
				while (!MyQueue.isEmpty()){
					ComparableFriend temp = MyQueue.poll();
					if ( temp.value  < 0.5 && !( malicious && collusion )){ // Instance variable usage

						Followee_struct current = checkedService.get_followee(temp.id);
						checkedService.followee_remove(temp.id);
						this.removeLink(current.Sensor);
					}
				}
			}
		}
	}

	private  Outcome get_recommendation(String s) {
		Application_struct struct1=null;
		TemplateTRM_Sensor my_new_sensor =null;
		Application_struct recommenders = friends.get("recommendation");
		PriorityQueue<ComparableFriend> MyQueue=recommenders.rank_friends("trust");
		ArrayList<Followee_struct> recommendations =  new ArrayList<Followee_struct>();
		LinkedList<TemplateTRM_Sensor> recommender =  new LinkedList<TemplateTRM_Sensor>();
		while (!MyQueue.isEmpty() && recommendations.size()<4){
			ComparableFriend temp = MyQueue.poll();
			if ( temp.value > 0.5)
			{
				TemplateTRM_Sensor current = recommenders.get_followee(temp.id).Sensor;
				if (current!=null)  struct1 = current.get_app(s);
				if (struct1!=null) my_new_sensor = struct1.request_Sensor(collusion,malicious);
				if (my_new_sensor!=null && my_new_sensor.isActive()){Followee_struct   next =new Followee_struct(my_new_sensor);
					recommendations.add(next);    recommender.add(current);}
			}else break;
		}
		//Exw tis protaseis zhtaw feedback gia ton kathena kai vriskw reputation apo ton most reputable  ton kanw filo kai zhtaw service
		double reputation=0.0;
		for (Followee_struct current : recommendations){
			current.reputation = calculate_reputation(current.Sensor.id,s);
			// System.out.println(current.reputation);
			if (current.reputation > reputation) current.reputation = reputation;}
		for (Followee_struct current : recommendations){
			TemplateTRM_Sensor his_recommender = recommender.pollFirst();
			if (current.reputation ==  reputation)
			{     Application_struct Service = friends.get(s);
				addfollowee(current.Sensor,s,0.0);       //vazw filo kai zhtaw service
				Service.change_reputation(current.Sensor.id,s,current.reputation);
				MyOutcome outcome1=current.requestService(s);
				Service.addNewShare(Integer.toString(current.Sensor.id), outcome1);
				recommenders.addNewShare(Integer.toString(his_recommender.id), outcome1);//kanw evaluate ton arxiko recommender
				return outcome1;
			}
		}
		return null;
	}


	private double calculate_reputation(int id, String service) {
		//zhtaw feedback apo high reputable kai to vazw se lista mazi me to pios to edwse
		LinkedList<MyTransaction> feedback = new LinkedList<MyTransaction>();
		double m = 0,s=0;
		Application_struct recommenders = friends.get("recommendation");
		PriorityQueue<ComparableFriend> MyQueue=recommenders.rank_friends("trust");
		for(int i=0; i<5 && !MyQueue.isEmpty() ;i++ ){
			ComparableFriend temp = MyQueue.poll();
			double experience=recommenders.get_followee(temp.id).Sensor.ask_experience(Integer.toString(id),service);
			if (experience >=0){
				double weight=temp.value;
				feedback.add(new MyTransaction(new MyOutcome(new SatisfactionInterval(0.0, 1.0, experience)),weight,temp.id));}
		}
		// calculate intial reputation distribution
		double sum = 0.0;// wi*xi
		double weights = 0.0;// wi
		double wx2 = 0.0;// wi*xi^2
		int j=0;

		if (feedback.size()>0){
			for ( j = 0; j < feedback.size(); j++) {
				MyTransaction t = feedback.get(j);
				double weight = t.fading * t.severity;
				double xi = t.getSatisfaction().getSatisfactionValue();
				double temp = weight * xi;
				sum += temp; // wi*xi
				wx2 += temp * xi;// wi*xi*xi
				weights += weight;// wi
			}
			m = sum / weights;
			s = (wx2 * weights - Math.pow(sum, 2)) / Math.pow(weights, 2);
		}
		//next get more feedback
		for(int i=0; i<15 && !MyQueue.isEmpty() ;i++ ){
			ComparableFriend temp = MyQueue.poll();
			double experience=recommenders.get_followee(temp.id).Sensor.ask_experience(Integer.toString(id),service);
			if (experience >=0){
				double weight=temp.value;
				if ((experience < m-0.5*s) || (experience > m+0.5*s)) {
					recommenders.addNewShare(temp.id, (new MyOutcome(new SatisfactionInterval(0.0, 1.0, 0.3))));
				}
				else feedback.add(new MyTransaction(new MyOutcome(new SatisfactionInterval(0.0, 1.0, experience)),weight,temp.id));}
		}
		// System.out.println(feedback.size());
		if (feedback.size()>=j){
			for ( ; j < feedback.size(); j++) {
				MyTransaction t = feedback.get(j);
				double weight = t.fading * t.severity;
				double xi = t.getSatisfaction().getSatisfactionValue();
				double temp = weight * xi;
				sum += temp; // wi*xi
				wx2 += temp * xi;// wi*xi*xi
				weights += weight;// wi
			}
			m = sum / (weights+0.000001);
			s = (wx2 * weights - Math.pow(sum, 2)) / Math.pow(weights+0.00001, 2);
			for (int i=0; i<feedback.size();i++){
				MyTransaction t = feedback.get(i);
				if ((t.getSatisfaction().getSatisfactionValue() < m-0.5*s) || (t.getSatisfaction().getSatisfactionValue() > m+0.5*s)) {
					recommenders.addNewShare(t.id, (new MyOutcome(new SatisfactionInterval(0.0, 1.0, 0.0))));
				}else     recommenders.addNewShare(t.id, (new MyOutcome(new SatisfactionInterval(0.0, 1.0, 0.9))));
			}
			//zhtaw apo tous ypoloipous kai kanw evaluate
			//kane evaluate tous arxikous
			////System.out.println("m-"+m);
			return m-s;
		}
		else return -1;
	}

	public synchronized double ask_experience(String string, String service) {

		Application_struct f = friends.get(service);
		if (f!=null)   {
			Followee_struct f1 = f.get_followee(string);
			if (f1!=null){
				if(malicious && collusion){
					return 1-f1.return_trust();
				}else return f1.return_trust();

			}
		}

		return -0.1;
	}

	public synchronized Application_struct get_app(String S){
		return friends.get(S);
	}


	public synchronized Outcome get_assistance(String s, int i,int id) {
		Application_struct recommenders = friends.get("recommendation");
		PriorityQueue<ComparableFriend> MyQueue=recommenders.rank_friends("assists");

		while (!MyQueue.isEmpty() && outcome ==  null){
			ComparableFriend temp = MyQueue.poll();
			if ( temp.value > 0.5)
			{
				if(collusion && (id!=this.id) && malicious){ // Using instance variable collusion

					Application_struct bad_struct = get_app(s);
					if (bad_struct!=null){
						PriorityQueue<ComparableFriend> BadQueue= bad_struct.rank_friends("trust");
						while (!BadQueue.isEmpty()){
							ComparableFriend bad_temp = BadQueue.poll();
							if (bad_temp.value < 0.3){ //found bad friend
								Followee_struct ff = bad_struct.get_followee(temp.id);
								if (ff!=null){
									TemplateTRM_Sensor current = ff.Sensor;
									Application_struct new_requiredService = current.get_app(s);
									if (new_requiredService!=null){
										outcome = new_requiredService.request_Service();}
								}}
						}
					}
				}else
				{
					TemplateTRM_Sensor current = recommenders.get_followee(temp.id).Sensor;
					Application_struct new_requiredService = current.get_app(s);
					if (new_requiredService!=null){

						outcome = new_requiredService.request_Service();
					}if (i>0 && outcome==null) outcome = current.get_assistance(s, i-1,this.id);
				}

				if (outcome!=null){
					recommenders.addNewAssist(temp.id,(MyOutcome)outcome);}
			}else break;

		}
		return outcome; // OPRAVENÉ: Return je teraz vnútri metódy
	}

	private synchronized void report() {
		Set<String> Services = getRequestedServices();

		for (String s : Services)// gia kathe service pou thelw
		{
			if (s.equals("My service")) {

				Application_struct reportedService = friends.get(s);
				platform.report(reportedService,this.serve(s),collusion); // Using instance variable collusion

			}
		}
	}

	/**
	 * Adds a new service to the set of offered services of this sensor
	 *
	 * @param service
	 * The new service to be added
	 * @param goodness
	 * The goodness when offering that new service
	 */
	@Override
	public void addService(Service service, double goodness) {
		servicesGoodness2.put(service.id, Double.valueOf(goodness)); // Fixed
		servicesGoodness.put(service, Double.valueOf(goodness)); // Fixed

	}

	public synchronized double serve(String service) {
		numRequests++;
		if (numRequests == numRequestsThreshold) {
			numRequests = 0;
			if (dynamic && runningSimulation) {
				activeState = false;
				numRequestsTimer = new Timer();
				numRequestsTimer.schedule(new TimerTask(){
					@Override
					public void run() {
						activeState = true;
						numRequestsTimer.cancel();
					}
				},sleepingTimeoutMilis);
			}
		}
		if(servicesGoodness2.get(service)!=null && this.isActive()){
			double quality = servicesGoodness2.get(service);
			return Math.round(10 * (quality)) / 10.0;}
		else return -0.1;
	}

	@Override
	public void reset() {
		friends = new HashMap<String, Application_struct>();
	}

	/**
	 * Indicates if there is a collusion or not
	 *
	 * @return true, if there is a collusion, false otherwise
	 */
	public boolean collusion() { // OPRAVENÉ: Už nie je static
		return collusion;
	}

	/**
	 * Returns the service requested by the client
	 *
	 * @return The service requested by the client
	 */
	public Service get_requiredService() {
		return requiredService;
	}
	@Override
	public void set_goodness(Service service, double goodness) throws Exception {
		if (!offersService(service.id()))
			throw new Exception("Server "+id+" doesn't offer service "+service.id());

		servicesGoodness.put(service, Double.valueOf(goodness)); // Fixed
		servicesGoodness2.put(service.id, Double.valueOf(goodness)); // Fixed

	}

	/**
	 * Sets the window size for storing transactions outcomes
	 *
	 * @param windowSize
	 * New window size for storing transactions outcomes
	 */
	public static void set_windowSize(int windowSize) {
		_windowSize = windowSize;
	}
}