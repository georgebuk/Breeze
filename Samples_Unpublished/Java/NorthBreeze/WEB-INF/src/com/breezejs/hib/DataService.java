package com.breezejs.hib;

import java.util.List;

import javax.ws.rs.core.Response;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.jboss.logging.Logger;
import com.breezejs.OdataParameters;
import com.breezejs.QueryResult;
import com.breezejs.save.ContextProvider;
import com.breezejs.save.SaveResult;
import com.breezejs.util.Json;

public class DataService {
	
	private static String metadataJson; 
	public static final Logger log = Logger.getLogger("NorthBreeze");

	public String getMetadata() {
		if (metadataJson == null) {
			metadataJson = Json.toJson(StaticSessionFactory.getMetadataMap(), false, false);
		}
		return metadataJson;
	}

	public String queryToJson(Class clazz, String queryString) {
    	OdataParameters op = OdataParameters.parse(queryString);
    	return queryToJson(clazz, op);
	}
	
	public String queryToJson(Class clazz, OdataParameters op) {
//    	op = OdataParameters.parse("http://localhost:7149/breeze/DemoNH/Customers?$top=3&$expand=Orders");
//    	op = OdataParameters.parse("$top=3&$select=Country,PostalCode&$inlinecount=allpages");
		log.infov("queryToJson: class={0}, odataParameters={1}", clazz, op);

		Session session = StaticSessionFactory.openSession();
		try {
			session.beginTransaction();
	    	
	    	Criteria crit = session.createCriteria(clazz);
	    	
	    	// Here, we could apply filtering to criteria based on e.g. user id...
	    	
	    	// Apply OData parameters to the Criteria
	    	OdataCriteria.applyParameters(crit, op);
			log.infov("queryToJson: criteria={0}", crit);

	    	String json = queryToJson(crit, op.hasInlineCount(), op.expands());
	    	
			session.getTransaction().commit();
			return json;
		}
    	catch (RuntimeException e) {
    		session.getTransaction().rollback();
    	    throw e; // or display error message
    	}
    	finally {
    		session.close();
    	}    	
	}
	
	public String queryToJson(Criteria crit, boolean inlineCount, String[] expands) {
		List result = crit.list();
		log.infov("queryToJson: result size={0}", result.size());
		
		if (expands != null && expands.length > 0) {
			HibernateExpander.initializeList(result, expands);
		}
		
		String json;
		if (inlineCount) {

			OdataCriteria.applyInlineCount(crit);
			long countResult = (long) crit.uniqueResult();
			log.infov("queryToJson: inline count={0}", countResult);
			
			QueryResult qr = new QueryResult(result, countResult);
			json = Json.toJson(qr);
			
		} else {
			json = Json.toJson(result);
		}
		
		log.infov("queryToJson: result={0}", json);
		return json;
	}
	
	/**
	 * Execute an HQL query and return the results as JSON
	 * @param hqlQuery
	 * @return
	 */
	public String queryToJson(String hqlQuery) {

		Session session = StaticSessionFactory.openSession();
		try {
			session.beginTransaction();
			List result = session.createQuery(hqlQuery).list();
			session.getTransaction().commit();
			return Json.toJson(result);
		}
    	catch (RuntimeException e) {
    		session.getTransaction().rollback();
    	    throw e; // or display error message
    	}
    	finally {
    		session.close();
    	}    	
	}

	/**
	 * Save the changes and return a response indicated the updated entities
	 * or errors
	 * @param source
	 * @return
	 */
	public Response saveChanges(String source) {
		log.infov("saveChanges", "source={0}", source);
		Response response;
		Session session = StaticSessionFactory.openSession();
		try {
			ContextProvider context = new HibernateContext(session, StaticSessionFactory.getMetadataMap());
			SaveResult sr = context.saveChanges(source);
			
			String json = Json.toJson(sr);
			log.infov("saveChanges: SaveResult={0}", json);
			if (sr.hasErrors()) {
				response = Response.status(Response.Status.FORBIDDEN).entity(json).build(); 
			} else {
				response = Response.ok(json).build();
			}
		}
    	catch (Exception e) {
    		log.errorv(e, "saveChanges: source={0}", source);
    		String json = Json.toJson(e);
			response = Response.serverError().entity(json).build(); 
    	}
    	finally {
    		session.close();
    	}    	
		return response;
	}
	
	
	/**
	 * For debugging
	 */
    public static void main(String[] args) throws Exception {
    	DataService ds = new DataService();
    	
//    	String saveBundle = "{'entities':[{'customerID':'04fa5e78-f2cb-d74d-a7de-083ee17b6ad2','rowVersion':3,'customerID_OLD':'ALFKI','companyName':'Alfreds Futterkiste','contactName':'Maria K. Anders','contactTitle':'Sales Representativ','address':'Obere Str. 57','city':'Berlin','region':'Ost','postalCode':'12209','country':'Germany','phone':'030-0074321','fax':'030-0076545','entityAspect':{'entityTypeName':'Customer:#northwind.model','defaultResourceName':'Customers','entityState':'Modified','originalValuesMap':{'contactTitle':'Sales Representative','rowVersion':2},'autoGeneratedKey':{'propertyName':'customerID','autoGeneratedKeyType':'KeyGenerator'}}}],'saveOptions':{}}";
//    	String saveBundle = "{'entities':[{'customerID':'857b40ea-21a3-ba4a-bcd8-af44d3d37418','rowVersion':3,'customerID_OLD':'BERGS','companyName':'Berglunds snabbk�p','contactName':'Error Message?','contactTitle':'Order Administrator','address':'Berguvsv�gen  8','city':'Lule�','region':null,'postalCode':'S-958 22','country':'Sweden','phone':'0921-12 34 65','fax':'0921-12 34 67','entityAspect':{'entityTypeName':'Customer:#northwind.model','defaultResourceName':'Customers','entityState':'Modified','originalValuesMap':{'contactName':'Error message','rowVersion':2},'autoGeneratedKey':{'propertyName':'customerID','autoGeneratedKeyType':'KeyGenerator'}}}],'saveOptions':{}}";
//    	ds.log.debug(ds.saveChanges(saveBundle));
//    	String meta = ds.getMetadata();
//    	ds.log(meta);
//    	Session session = NorthwindSessionFactory.openSession();
    	
//    	OdataParameters op = OdataParameters.parse("?$top=5&$filter=Country eq 'Brazil'");
//    	op = OdataParameters.parse("http://localhost:7149/breeze/DemoNH/Customers?$top=3&$expand=Orders");
//    	op = OdataParameters.parse("$top=3&$select=Country,PostalCode&$inlinecount=allpages");
//    	log(op);
    	
//    	Criteria crit = session.createCriteria(Customer.class);
//    	OdataCriteria.applyParameters(crit, op);
    	
//    	nb.queryToJson(Customer.class, "?$top=5&$filter=country eq 'Brazil'");
//    	ds.queryToJson(northwind.model.Customer.class, "?$top=5&$filter=country eq 'Brazil'&$inlinecount=allpages");
    	ds.log.infov(ds.queryToJson(northwind.model.Customer.class, "?$top=5&$filter=country eq 'Brazil'&$expand=orders/orderDetails/product"));
    	System.exit(0);
    }
    
	
}
