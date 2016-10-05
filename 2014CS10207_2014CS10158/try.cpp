#include <bits/stdc++.h>

using namespace std;

inline int foo(int a){
	a+=100;
	int c=0;
	return a;
}

int main(){

	cout<<"CLIENT ASS INITIALISED"<<endl;
	int c=10,a=20;
	int b = foo(0);
	
	cout<<"a: "<<a<<" | b: "<<b<<" | c: "<<c<<endl;
	return 0;
}
