	include include1.asm, include3.asm

init:	ld a, 0
	mac1
	mac3
	ld a, 4
	end init
