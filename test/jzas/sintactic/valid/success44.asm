		aseg
		org 0F80h
start:		ld HL, 1000h
		ld C, 1020h-1000h
ciclo:		ld A, (HL)
		cp 'Z'+1
		jp P, sigo
		cp 'A'
		jp M, sigo
		add A, 'f'-'F'
		ld (HL), A
sigo:		inc HL
		dec C
		jr NZ, ciclo
		rst 38h
		end start
