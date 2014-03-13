mac1	macro
	ld a, 5
	mac2
	endm
mac2	macro
	ld a, 6
	endm

init	ld a, 0
	ld a, 1
	ld a, 2
	ld a, 3
	ld a, 4
	mac1
	ld a, 7
	ld a, 8
	ld a, 9
	ld a, 10
