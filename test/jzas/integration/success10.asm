init:	aseg
	org 100h
	ld a, 0
	org 104h
	ld a, 1
	org 108h
	ld a, 2
	org 10Ch
	ld a, 3
	org 102h
	ld b, 0
	org 106h
	ld b, 1
	org 10Ah
	ld b, 2
	org 10Eh
	ld b, 3
	end init
