mac0	macro
	ld a, 0
	mac1
	endm

mac1	macro
	ld a, 1
	mac2
	endm

mac2	macro
	ld a, 2
	mac3
	endm

mac3	macro
	ld a, 3
	mac4
	endm

mac4	macro
	ld a, 4
	mac5
	endm

mac5	macro
	ld a, 5
	mac6
	endm

mac6	macro
	ld a, 6
	mac7
	endm

mac7	macro
	ld a, 7
	mac8
	endm

mac8	macro
	ld a, 8
	mac9
	endm

mac9	macro
	ld a, 9
	mac10
	endm

mac10	macro
	ld a, 10
	endm

init	mac0
