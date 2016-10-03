#include <bits/stdc++.h>
#include "BoardUnit.h"

using namespace std;

BoardUnit::BoardUnit(){
	color = 0;
	kind = 0;
}

BoardUnit::BoardUnit(int bucolor, int bukind){
	color = bucolor;
	kind = bukind;
}