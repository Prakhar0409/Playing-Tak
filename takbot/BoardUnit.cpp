#include <bits/stdc++.h>
#include "BoardUnit.h"

using namespace std;

BoardUnit::BoardUnit(){
	color = 0;		// 1=white,2=black
	kind = 0;		//1=flatstone,2=wall,3=capstone
}

BoardUnit::BoardUnit(int bucolor, int bukind){
	color = bucolor;
	kind = bukind;
}