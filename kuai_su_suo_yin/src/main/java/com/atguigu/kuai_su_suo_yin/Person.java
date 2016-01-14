package com.atguigu.kuai_su_suo_yin;

public class Person implements Comparable<Person>{

	private String name;
	private String pinyin;

	public Person(String name) {
		this.name = name;
		this.pinyin = PinYinUtils.getPinYin(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", pinyin=" + pinyin + "]";
	}



	/*
	>0
	=0
	<0
	 */
	@Override
	public int compareTo(Person another) {
		return pinyin.compareTo(another.pinyin);
	}

}
