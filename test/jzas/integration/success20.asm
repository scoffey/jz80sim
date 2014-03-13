init	dseg
	ld a, 0
	cseg
	org 2h
	ld a, 1
	dseg
	org 4h
	ld a, 2
	cseg
	org 6h
	ld a, 3
	end init
