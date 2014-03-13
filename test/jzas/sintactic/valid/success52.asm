		aseg
		org 1FF0h
start:		ld HL, 2000h
		ld B, 3
		xor A
ciclo:		add A, (HL)
		inc HL
calculo:	djnz ciclo
		rst 38h
		end start
