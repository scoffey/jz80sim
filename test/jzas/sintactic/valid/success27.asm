		aseg
		org 3000h
carga		macro reg, zona
		ld (aux), A
		ld A, (zona)
		ld reg, A
		ld A, (aux)
		jr aux+1
aux:		ds 1
		endm
descarga	macro reg, zona
		ld (aux), A
		ld A, reg
		ld (zona), A
		ld A, (aux)
		jr aux+1
aux:		ds 1
		endm
start:		nop
		carga B, dato
		inc B
		descarga B, rta1
		inc B
		descarga B, rta2
		rst 38h
dato:		db 5
rta1:		ds 1
rta2:		ds 1
		end start
