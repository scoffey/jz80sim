const0		equ 0
const10		equ 10
const20		defl 15
const20 	defl 16	; Se corrige el valor
const20 	defl 20	; Se corrige el valor
letraa		equ 'a'
letraA		equ 'A'
num100h		equ 99h + 1

rot0		ld a, 10*3/2 + 5*(1 + 1)
rot1		ld a, $ + letraa
rot2		ld a, letraA
		ld a, rot0 - rot1 + 4
		ld a, const0 + const10 + const20
		ld a, const20 + 10 - 5*2
		ld a, const20 + 2*($+1)
