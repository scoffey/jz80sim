		aseg
		org 0F80h
start:		ld A, (1000h)
		ld B, A
		inc B
		xor A
		ld HL, 1001h
		jp calculo
ciclo:		ld C, (HL)
		add A, C
		inc HL
calculo:	djnz ciclo
		ld (0FFFh), A
		rst 38h
		end start
