		aseg
		org 20D0h

start:		ld E, B	; resguardo B

		xor A
		ld B, 2
cicloC:		add C
		djnz cicloC
		ld C, A

		xor A
		ld B, 5
cicloD:		add D
		djnz cicloD
		ld D, A

		xor A
		ld B, 4
cicloB:		add E
		djnz cicloB
		ld B, A

		rst 38h
		end start
