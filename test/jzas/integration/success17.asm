rotA 	equ 1
rotA	equ 1
rotA	defl 2
rotA	equ 2
rotA 	defl 3
rotA	equ 3

rotB	defl 1
rotB	defl 2
rotB	equ 3
rotB	defl 4
rotB	defl 4

	ld a, rotA
	ld b, rotB
	ld a, rotA + rotB
