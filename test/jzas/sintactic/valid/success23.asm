		aseg
		org 0F80h
start:		ld A, (1000h)
		ld B, A
		inc B
		xor A
		ld C, 0	; ahora C acumula la suma
		ld HL, 1001h
		jp calculo
ciclo:		ld A, (HL)
		cp 0
		jp M, paso
		add A, C
		ld C, A
paso:		inc HL
calculo:	djnz ciclo
		ld (0FFFh), A
		rst 38h
		end start
