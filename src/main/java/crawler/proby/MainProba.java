package crawler.proby;

import java.lang.reflect.Field;

public class MainProba {

	public static void main(String[] args) {
		Proba proba = new Proba();
		showParam(proba.getClass());

	}
	public static <T> void showParam(Class<T> clazz){
		Field[] fields = clazz.getDeclaredFields();
		for(Field field:fields){
			System.out.println(field.getName()+ ", "+field.getType().getName());
		}
		
	}

}
