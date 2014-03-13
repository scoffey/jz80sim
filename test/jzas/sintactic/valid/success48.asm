		aseg
		org 3000h

aMayuscula	macro rotuloLetra
		ld (aux), A
		ld A, (rotuloLetra)
		cp 'a'
		jp M, paso
		cp 'z'+1
		jp P, paso
		add A,'A'-'a'
		ld (rotuloLetra), A
paso:		ld A, (aux)
		jr aux+1
aux:		ds 1
		endm

start:		ld IX, cadena
		ld B, largo+1
		jp djnz1
ciclo:		aMayuscula IX
		inc IX
djnz1:		djnz ciclo
		rst 38h

cadena:		defm "eStO eS uNa PruEba."
largo:		equ $-cadena
		end start
