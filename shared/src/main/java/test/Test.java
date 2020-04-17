package test;

public class Test {
	public static void main(String... args) {
		#if MC_7 System.out.println("MC7"); #endif
		#if MC_8 System.out.println("MC8"); #endif
		#if MC_10 System.out.println("MC10"); #endif
		#if MC_12 System.out.println("MC12"); #endif
		#if MC_14 System.out.println("MC14"); #endif
		#if MC_15 System.out.println("MC15"); #endif
	}
}