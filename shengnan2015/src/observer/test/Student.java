package observer.test;

public class Student implements Observer {
	private String name;      
	private String phone;      
	private Teacher teacher;      
	public Student(String name,Teacher t){
		this.name = name;
		teacher = t;      
	}      
	public void show(){         
	System.out.println("Name:"+name+"\nTeacher'sphone:"+phone);      
	}   
	@Override
	public void update() {
		phone = teacher.getPhone();
	}

}
