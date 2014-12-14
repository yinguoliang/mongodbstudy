import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


public class Main {
	public static Person buildPerson(){
		Person p = new Person();
		p.setName("YYYY");
		p.setAge(100);
		p.setBirthDay(new Date());
		Addr addr = new Addr();
		addr.setNation("CN");
		addr.setProvince("sh");
		addr.setCity("sh");
		addr.setStreet("bohua");
		p.setAddr(addr);
		return p;
	}
	public static DBObject mashallObject(Object obj) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(obj==null) return null;
		DBObject dbObj = new BasicDBObject();
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field : fields){
			field.setAccessible(true);
			Object val = field.get(obj);
			if(field.getType().isPrimitive() 
					|| field.getType().getName().startsWith("java.lang")){
				dbObj.put(field.getName(), val);
			}else if(field.getType().getName().indexOf("Date")>-1){
				dbObj.put(field.getName(), sdf.format((Date)val));
			}else{
				dbObj.put(field.getName(), mashallObject(val));
			}
		}
		return dbObj;
	}
	public static void main(String[] args) throws Exception {
		Mongo mongo = new Mongo("localhost",27017);
		DB db = mongo.getDB("wetest");
		DBCollection users = db.getCollection("users");
		DBCursor cur = users.find();
		while(cur.hasNext()){
			System.out.println(cur.next());
		}
		Person p = buildPerson();
		//System.out.println(mashallObject(p));
		users.insert(mashallObject(p));
		System.out.println(users.getCount());
	}

}
